package com.tonggn.msspt.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import com.tonggn.msspt.catalog.domain.product.GoodsNo;
import com.tonggn.msspt.catalog.domain.product.Product;
import com.tonggn.msspt.catalog.domain.product.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(value = "classpath:clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ProductUpdateServiceTest {

  @Autowired
  private ProductUpdateService productUpdateService;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private NamedParameterJdbcTemplate namedJdbc;

  @Test
  @DisplayName("상품이 존재하지 않을 경우 상품 정보와 최근 가격을 저장한다.")
  void saveAndUpdateWithNewProductsTest() {
    // given
    final List<Integer> expectPrices = List.of(1000, 2000);
    final List<ProductUpdateRequest> requests = List.of(
        new ProductUpdateRequest(new GoodsNo(1L), "name", 1000, expectPrices.get(0),
            "imageUrl", new BrandId("brand"), new CategoryId("category")),
        new ProductUpdateRequest(new GoodsNo(2L), "name", 2000, expectPrices.get(1),
            "imageUrl", new BrandId("brand"), new CategoryId("category"))
    );
    final List<Product> expects = requests.stream()
        .map(request -> {
          final Product product = mapToProduct(request);
          product.addLastPrice(request.price());
          return product;
        })
        .toList();

    // when
    productUpdateService.saveOrUpdateProducts(requests);

    // then
    final List<Product> actualProducts = productRepository.findAll();
    assertThat(actualProducts)
        .usingRecursiveComparison()
        .ignoringFields("id", "priceDetails.priceHistories", "createdAt", "updatedAt")
        .isEqualTo(expects);

    final MapSqlParameterSource ids = new MapSqlParameterSource("ids",
        actualProducts.stream().map(Product::getId).toList());
    final List<Integer> actualPrices = namedJdbc.query(
        "select product_id, price from price_history where product_id in (:ids)", ids,
        (rs, rowNum) -> rs.getInt("price"));
    assertThat(actualPrices).isEqualTo(expectPrices);
  }

  private Product mapToProduct(final ProductUpdateRequest request) {
    return new Product(request.goodsNo(), request.name(), request.normalPrice(), request.price(),
        request.imageUrl(), request.brandId(), request.categoryId());
  }
}

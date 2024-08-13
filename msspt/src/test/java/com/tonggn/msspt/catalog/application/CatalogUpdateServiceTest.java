package com.tonggn.msspt.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.brand.BrandRepository;
import com.tonggn.msspt.catalog.domain.category.Category;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import com.tonggn.msspt.catalog.domain.category.CategoryRepository;
import com.tonggn.msspt.catalog.domain.product.Product;
import com.tonggn.msspt.catalog.domain.product.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CatalogUpdateServiceTest {

  @Autowired
  private CatalogUpdateService catalogUpdateService;

  @Autowired
  private BrandRepository brandRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private NamedParameterJdbcTemplate namedJdbc;

  @BeforeEach
  void setUp() {
    brandRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  @DisplayName("상품 목록을 업데이트할 카테고리 목록을 조회한다.")
  void getCategoriesTest() {
    // given
    final List<CategoryResponse> expect = List.of(
        new CategoryResponse("A", "카테고리A"),
        new CategoryResponse("B", "카테고리B")
    );

    categoryRepository.saveAll(List.of(
        new Category(new CategoryId("A"), "카테고리A"),
        new Category(new CategoryId("B"), "카테고리B")
    ));

    // when
    final List<CategoryResponse> actual = catalogUpdateService.getCategories();

    // then
    assertThat(actual).isEqualTo(expect);
  }

  @Test
  @DisplayName("상품이 존재하지 않을 경우 상품 정보와 최근 가격을 저장한다.")
  void saveAndUpdateWithNewProductsTest() {
    // given
    final List<Integer> expectPrices = List.of(1000, 2000);
    final List<ProductUpdateRequest> requests = List.of(
        new ProductUpdateRequest(1L, "name", 1000, expectPrices.get(0),
            "imageUrl", "brand", "category"),
        new ProductUpdateRequest(2L, "name", 2000, expectPrices.get(1),
            "imageUrl", "brand", "category")
    );
    final List<Product> expects = requests.stream()
        .map(request -> {
          final Product product = mapToProduct(request);
          product.addLastPriceIfNew(request.price());
          return product;
        })
        .toList();

    // when
    catalogUpdateService.saveAndUpdateProducts(requests);

    // then
    final List<Product> actualProducts = productRepository.findAll();
    assertThat(actualProducts)
        .usingRecursiveComparison()
        .ignoringFields("id", "priceHistories", "createdAt", "updatedAt")
        .isEqualTo(expects);

    final MapSqlParameterSource ids = new MapSqlParameterSource("ids",
        actualProducts.stream().map(Product::getId).toList());
    final List<Integer> actualPrices = namedJdbc.query(
        "select product_id, price from price_history where product_id in (:ids)", ids,
        (rs, rowNum) -> rs.getInt("price"));
    assertThat(actualPrices).isEqualTo(expectPrices);
  }

  @Test
  @DisplayName("상품이 존재하고 마지막 가격과 최근 가격이 다를 경우 최근 가격을 추가한다.")
  void saveAndUpdateWithExistsProductsTest() {
    // given
    final List<Integer> expectPrices = List.of(2000, 2000, 1000, 1500);
    final List<ProductUpdateRequest> expectRequests = List.of(
        new ProductUpdateRequest(1L, "name", 2000, expectPrices.get(0), "imageUrl", "brand",
            "category"),
        new ProductUpdateRequest(2L, "name", 2000, expectPrices.get(1), "imageUrl", "brand",
            "category")
    );
    final List<Product> expectProducts = expectRequests.stream()
        .map(request -> {
          final Product product = mapToProduct(request);
          product.addLastPriceIfNew(request.price());
          return product;
        })
        .toList();
    productRepository.saveAll(expectProducts);

    final List<ProductUpdateRequest> updateRequests = List.of(
        new ProductUpdateRequest(1L, "name", 2000, expectPrices.get(2), "imageUrl", "brand",
            "category"),
        new ProductUpdateRequest(2L, "name", 2000, expectPrices.get(3), "imageUrl", "brand",
            "category")
    );

    // when
    catalogUpdateService.saveAndUpdateProducts(updateRequests);

    // then
    final List<Product> actualProducts = productRepository.findAll();
    assertThat(actualProducts)
        .usingRecursiveComparison()
        .ignoringFields("id", "priceHistories", "createdAt", "updatedAt")
        .isEqualTo(expectProducts);

    final MapSqlParameterSource ids = new MapSqlParameterSource("ids",
        actualProducts.stream().map(Product::getId).toList());
    final List<Integer> actualPrices = namedJdbc.query(
        "select product_id, price from price_history where product_id in (:ids)", ids,
        (rs, rowNum) -> rs.getInt("price"));
    assertThat(actualPrices).isEqualTo(expectPrices);
  }

  @Test
  @DisplayName("상품이 존재하고 마지막 가격과 최근 가격이 같을 경우 최근 가격을 추가하지 않는다.")
  void saveAndUpdateWithExistsAndSamePriceProductsTest() {
    // given
    final List<Integer> expectPrices = List.of(2000, 2000);
    final List<ProductUpdateRequest> expectRequests = List.of(
        new ProductUpdateRequest(1L, "name", 2000, expectPrices.get(0), "imageUrl", "brand",
            "category"),
        new ProductUpdateRequest(2L, "name", 2000, expectPrices.get(1), "imageUrl", "brand",
            "category")
    );
    final List<Product> expectProducts = expectRequests.stream()
        .map(request -> {
          final Product product = mapToProduct(request);
          product.addLastPriceIfNew(request.price());
          return product;
        })
        .toList();
    productRepository.saveAll(expectProducts);

    // when
    catalogUpdateService.saveAndUpdateProducts(expectRequests);

    // then
    final List<Product> actualProducts = productRepository.findAll();
    assertThat(actualProducts)
        .usingRecursiveComparison()
        .ignoringFields("id", "priceHistories", "createdAt", "updatedAt")
        .isEqualTo(expectProducts);

    final MapSqlParameterSource ids = new MapSqlParameterSource("ids",
        actualProducts.stream().map(Product::getId).toList());
    final List<Integer> actualPrices = namedJdbc.query(
        "select product_id, price from price_history where product_id in (:ids)", ids,
        (rs, rowNum) -> rs.getInt("price"));
    assertThat(actualPrices).isEqualTo(expectPrices);
  }

  private Product mapToProduct(final ProductUpdateRequest request) {
    return new Product(request.goodsNo(), request.name(), request.normalPrice(), request.imageUrl(),
        new BrandId(request.brandId()), new CategoryId(request.categoryId()));
  }
}

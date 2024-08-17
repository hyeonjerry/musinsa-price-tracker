package com.tonggn.msspt.catalog.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = "classpath:clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("goodsNo로 상품과 가격 이력을 조회한다.")
  void findByGoodsNoInWithPriceHistoriesTest() {
    // given
    final List<Product> expect = List.of(
        new Product(1L, "name", 2000, "imageUrl", new BrandId("brand"), new CategoryId("category")),
        new Product(2L, "name", 2000, "imageUrl", new BrandId("brand"), new CategoryId("category")),
        new Product(3L, "name", 2000, "imageUrl", new BrandId("brand"), new CategoryId("category"))
    );
    expect.forEach(product -> product.addLastPriceIfNew(1000));
    productRepository.saveAll(expect);

    // when
    final List<Long> goodsNos = expect.stream()
        .map(Product::getGoodsNo)
        .toList();
    final List<Product> actual = productRepository.findByGoodsNoInWithPriceHistories(goodsNos);

    // then
    assertThat(actual).usingRecursiveAssertion()
        .ignoringFields("id", "createdAt", "updatedAt")
        .isEqualTo(expect);
  }
}

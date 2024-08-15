package com.tonggn.msspt.catalog.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
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
  void findByGoodsNoWithPriceHistoriesTest() {
    // given
    final long goodsNo = 1L;
    final BrandId brandId = new BrandId("brand");
    final CategoryId categoryId = new CategoryId("category");
    final Product expect = new Product(goodsNo, "name", 2000, "imageUrl", brandId, categoryId);
    expect.addLastPriceIfNew(2000);
    expect.addLastPriceIfNew(3000);
    productRepository.save(expect);

    // when
    final Product actual = productRepository.findByGoodsNoWithPriceHistories(goodsNo).get();

    // then
    assertThat(actual).usingRecursiveAssertion()
        .ignoringFields("id", "createdAt", "updatedAt")
        .isEqualTo(expect);
  }
}

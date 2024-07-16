package com.tonggn.msspt.catalog.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("goodsNo로 상품을 조회한다.")
  void findByGoodsNoTest() {
    // given
    final long goodsNo = 1L;
    final BrandId brandId = new BrandId("brand");
    final CategoryId categoryId = new CategoryId("category");
    final Product expect = new Product(goodsNo, "name", 2000, brandId, categoryId);
    productRepository.save(expect);

    // when
    final Product actual = productRepository.findByGoodsNo(goodsNo).get();

    // then
    assertThat(actual).usingRecursiveAssertion()
        .ignoringFields("id", "createdAt", "updatedAt")
        .isEqualTo(expect);
  }
}

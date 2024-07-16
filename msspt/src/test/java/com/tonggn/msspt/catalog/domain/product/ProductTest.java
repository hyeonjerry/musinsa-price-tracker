package com.tonggn.msspt.catalog.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

  @Test
  @DisplayName("Product에 최근 가격을 추가한다.")
  void addLastPriceTest() {
    // given
    final BrandId brand = new BrandId("brand");
    final CategoryId category = new CategoryId("category");
    final Product product = new Product(1L, "name", 2000, brand, category);

    // when
    product.addLastPrice(1000);

    // then
    final List<PriceHistory> actual = product.getPriceHistories();
    final List<PriceHistory> expect = List.of(new PriceHistory(product, 1000));
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}

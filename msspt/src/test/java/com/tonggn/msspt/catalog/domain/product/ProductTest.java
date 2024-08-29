package com.tonggn.msspt.catalog.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductTest {

  @ParameterizedTest
  @ValueSource(ints = {1000, 2000, 3000})
  @DisplayName("Product에 최근 가격을 추가한다.")
  void addLastPriceTest(final int price) {
    // given
    final Product product = new Product(new GoodsNo(1L), "name", 2000, "imageUrl",
        new BrandId("brand"), new CategoryId("category"));

    // when
    product.addLastPrice(price);

    // then
    final List<PriceHistory> actual = product.getPriceHistories();
    final List<PriceHistory> expect = List.of(
        new PriceHistory(product, price)
    );
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}

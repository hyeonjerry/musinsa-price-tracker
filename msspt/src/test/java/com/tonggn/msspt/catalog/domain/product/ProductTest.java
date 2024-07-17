package com.tonggn.msspt.catalog.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

  @Test
  @DisplayName("최근 가격이 없을 경우 Product에 최근 가격을 추가한다.")
  void addLastPriceIfNewTest() {
    // given
    final BrandId brand = new BrandId("brand");
    final CategoryId category = new CategoryId("category");
    final Product product = new Product(1L, "name", 2000, "imageUrl", brand, category);

    // when
    product.addLastPriceIfNew(1000);

    // then
    final List<PriceHistory> actual = product.getPriceHistories();
    final List<PriceHistory> expect = List.of(new PriceHistory(product, 1000));
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("최근 가격이 있고 새로운 가격과 다를 경우 Product에 최근 가격을 추가한다.")
  void addLastPriceIfNewWithDifferentPriceTest() {
    // given
    final BrandId brand = new BrandId("brand");
    final CategoryId category = new CategoryId("category");
    final Product product = new Product(1L, "name", 2000, "imageUrl", brand, category);
    product.addLastPriceIfNew(1000);

    // when
    product.addLastPriceIfNew(1500);

    // then
    final List<PriceHistory> actual = product.getPriceHistories();
    final List<PriceHistory> expect = List.of(
        new PriceHistory(product, 1000),
        new PriceHistory(product, 1500)
    );
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("최근 가격이 있고 새로운 가격과 동일할 경우 Product에 최근 가격을 추가하지 않는다.")
  void addLastPriceIfNewWithSamePriceTest() {
    // given
    final BrandId brand = new BrandId("brand");
    final CategoryId category = new CategoryId("category");
    final Product product = new Product(1L, "name", 2000, "imageUrl", brand, category);
    product.addLastPriceIfNew(1000);

    // when
    product.addLastPriceIfNew(1000);

    // then
    final List<PriceHistory> actual = product.getPriceHistories();
    final List<PriceHistory> expect = List.of(new PriceHistory(product, 1000));
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}

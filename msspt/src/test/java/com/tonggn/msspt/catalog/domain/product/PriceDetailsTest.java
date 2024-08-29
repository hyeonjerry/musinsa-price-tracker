package com.tonggn.msspt.catalog.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PriceDetailsTest {

  @Test
  @DisplayName("객체 생성시 필드를 기본 값으로 초기화한다.")
  void initializeFieldsTest() {
    // given
    final int expectNormalPrice = 10_000;
    final int expectCurrentPrice = 9_000;
    final LocalDate expectDate = LocalDate.now();

    // when
    final PriceDetails actual = new PriceDetails(expectNormalPrice, expectCurrentPrice);

    // then
    assertAll(
        () -> assertThat(actual.getNormalPrice()).isEqualTo(expectNormalPrice),
        () -> assertThat(actual.getLatestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getBeforeLatestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getWeeklyLowestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getWeeklyLowestDate()).isEqualTo(expectDate),
        () -> assertThat(actual.getWeeklyHighestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getWeeklyHighestDate()).isEqualTo(expectDate),
        () -> assertThat(actual.getMonthlyLowestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getMonthlyLowestDate()).isEqualTo(expectDate),
        () -> assertThat(actual.getMonthlyHighestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getMonthlyHighestDate()).isEqualTo(expectDate)
    );
  }

  @Test
  @DisplayName("가격 이력을 추가한다.")
  void addPriceHistoryTest() {
    // given
    final int newPrice = 8_000;
    final Product product = new Product();
    final PriceDetails priceDetails = new PriceDetails(10_000, 9_000);
    final List<PriceHistory> expected = List.of(new PriceHistory(product, newPrice));

    // when
    priceDetails.addLastPrice(product, newPrice);

    // then
    assertThat(priceDetails.getPriceHistories())
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("가격 이력을 추가할 경우 최근 가격과 직전 가격을 갱신한다.")
  void updateLatestPriceTest() {
    // given
    final int beforePrice = 9_000;
    final int latestPrice = 8_000;
    final PriceDetails priceDetails = new PriceDetails(10_000, beforePrice);

    // when
    priceDetails.addLastPrice(new Product(), latestPrice);

    // then
    assertAll(
        () -> assertThat(priceDetails.getLatestPrice()).isEqualTo(latestPrice),
        () -> assertThat(priceDetails.getBeforeLatestPrice()).isEqualTo(beforePrice)
    );
  }

  @ParameterizedTest
  @ValueSource(ints = {8_999, 8_000})
  @DisplayName("추가한 가격 이력이 기간별 최저가보다 작을 경우 기간별 최저가 정보를 갱신한다.")
  void updateLowestPrice(final int newLowestPrice)
      throws NoSuchFieldException, IllegalAccessException {
    // given
    final PriceDetails priceDetails = new PriceDetails(10_000, 9_000);

    final LocalDate beforeDate = LocalDate.now().minusDays(1);
    final Field weeklyLowestDate = PriceDetails.class.getDeclaredField("weeklyLowestDate");
    weeklyLowestDate.setAccessible(true);
    weeklyLowestDate.set(priceDetails, beforeDate);
    final Field monthlyLowestDate = PriceDetails.class.getDeclaredField("monthlyLowestDate");
    monthlyLowestDate.setAccessible(true);
    monthlyLowestDate.set(priceDetails, beforeDate);

    // when
    priceDetails.addLastPrice(new Product(), newLowestPrice);

    // then
    assertAll(
        () -> assertThat(priceDetails.getWeeklyLowestPrice()).isEqualTo(newLowestPrice),
        () -> assertThat(priceDetails.getWeeklyLowestDate()).isEqualTo(LocalDate.now()),
        () -> assertThat(priceDetails.getMonthlyLowestPrice()).isEqualTo(newLowestPrice),
        () -> assertThat(priceDetails.getMonthlyLowestDate()).isEqualTo(LocalDate.now())
    );
  }

  @ParameterizedTest
  @ValueSource(ints = {9_000, 10_000})
  @DisplayName("추가한 가격 이력이 기간별 최저가보다 크거나 같을 경우 기간별 최저가 정보를 갱신하지 않는다.")
  void notUpdateLowestPrice(final int newPrice)
      throws IllegalAccessException, NoSuchFieldException {
    // given
    final int initialPrice = 9_000;
    final PriceDetails priceDetails = new PriceDetails(10_000, initialPrice);

    final LocalDate initialDate = LocalDate.now().minusDays(1);
    final Field weeklyLowestDate = PriceDetails.class.getDeclaredField("weeklyLowestDate");
    weeklyLowestDate.setAccessible(true);
    weeklyLowestDate.set(priceDetails, initialDate);
    final Field monthlyLowestDate = PriceDetails.class.getDeclaredField("monthlyLowestDate");
    monthlyLowestDate.setAccessible(true);
    monthlyLowestDate.set(priceDetails, initialDate);

    // when
    priceDetails.addLastPrice(new Product(), newPrice);

    // then
    assertAll(
        () -> assertThat(priceDetails.getWeeklyLowestPrice()).isEqualTo(initialPrice),
        () -> assertThat(priceDetails.getWeeklyLowestDate()).isEqualTo(initialDate),
        () -> assertThat(priceDetails.getMonthlyLowestPrice()).isEqualTo(initialPrice),
        () -> assertThat(priceDetails.getMonthlyLowestDate()).isEqualTo(initialDate)
    );
  }
}

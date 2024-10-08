package com.tonggn.msspt.catalog.command.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    assertAll(() -> assertThat(actual.getNormalPrice()).isEqualTo(expectNormalPrice),
        () -> assertThat(actual.getLatestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getBeforeLatestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getWeeklyLowestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getWeeklyLowestDate()).isEqualTo(expectDate),
        () -> assertThat(actual.getWeeklyHighestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getWeeklyHighestDate()).isEqualTo(expectDate),
        () -> assertThat(actual.getMonthlyLowestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getMonthlyLowestDate()).isEqualTo(expectDate),
        () -> assertThat(actual.getMonthlyHighestPrice()).isEqualTo(expectCurrentPrice),
        () -> assertThat(actual.getMonthlyHighestDate()).isEqualTo(expectDate));
  }

  @Nested
  @DisplayName("가격 이력 추가 테스트")
  class addPriceTest {

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
      assertThat(priceDetails.getPriceHistories()).usingRecursiveComparison().isEqualTo(expected);
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
      assertAll(() -> assertThat(priceDetails.getLatestPrice()).isEqualTo(latestPrice),
          () -> assertThat(priceDetails.getBeforeLatestPrice()).isEqualTo(beforePrice));
    }

    @ParameterizedTest
    @ValueSource(ints = {8_999, 8_000})
    @DisplayName("추가한 가격이 기간별 최저가보다 작을 경우 기간별 최저가 정보를 갱신한다.")
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
      assertAll(() -> assertThat(priceDetails.getWeeklyLowestPrice()).isEqualTo(newLowestPrice),
          () -> assertThat(priceDetails.getWeeklyLowestDate()).isEqualTo(LocalDate.now()),
          () -> assertThat(priceDetails.getMonthlyLowestPrice()).isEqualTo(newLowestPrice),
          () -> assertThat(priceDetails.getMonthlyLowestDate()).isEqualTo(LocalDate.now()));
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
      assertAll(() -> assertThat(priceDetails.getWeeklyLowestPrice()).isEqualTo(initialPrice),
          () -> assertThat(priceDetails.getWeeklyLowestDate()).isEqualTo(initialDate),
          () -> assertThat(priceDetails.getMonthlyLowestPrice()).isEqualTo(initialPrice),
          () -> assertThat(priceDetails.getMonthlyLowestDate()).isEqualTo(initialDate));
    }

    @ParameterizedTest
    @ValueSource(ints = {9_001, 10_000})
    @DisplayName("추가한 가격이 기간별 최고가보다 클 경우 기간별 최고가 정보를 갱신한다.")
    void updateHighestPrice(final int newHighestPrice)
        throws NoSuchFieldException, IllegalAccessException {
      // given
      final PriceDetails priceDetails = new PriceDetails(10_000, 9_000);

      final LocalDate beforeDate = LocalDate.now().minusDays(1);
      final Field weeklyHighestDate = PriceDetails.class.getDeclaredField("weeklyHighestDate");
      weeklyHighestDate.setAccessible(true);
      weeklyHighestDate.set(priceDetails, beforeDate);
      final Field monthlyHighestDate = PriceDetails.class.getDeclaredField("monthlyHighestDate");
      monthlyHighestDate.setAccessible(true);
      monthlyHighestDate.set(priceDetails, beforeDate);

      // when
      priceDetails.addLastPrice(new Product(), newHighestPrice);

      // then
      assertAll(() -> assertThat(priceDetails.getWeeklyHighestPrice()).isEqualTo(newHighestPrice),
          () -> assertThat(priceDetails.getWeeklyHighestDate()).isEqualTo(LocalDate.now()),
          () -> assertThat(priceDetails.getMonthlyHighestPrice()).isEqualTo(newHighestPrice),
          () -> assertThat(priceDetails.getMonthlyHighestDate()).isEqualTo(LocalDate.now()));
    }

    @ParameterizedTest
    @ValueSource(ints = {9_000, 8_999})
    @DisplayName("추가한 가격 이력이 기간별 최고가보다 작거나 같을 경우 기간별 최고가 정보를 갱신하지 않는다.")
    void notUpdateHighestPrice(final int newPrice)
        throws IllegalAccessException, NoSuchFieldException {
      // given
      final int initialPrice = 9_000;
      final PriceDetails priceDetails = new PriceDetails(10_000, initialPrice);

      final LocalDate initialDate = LocalDate.now().minusDays(1);
      final Field weeklyHighestDate = PriceDetails.class.getDeclaredField("weeklyHighestDate");
      weeklyHighestDate.setAccessible(true);
      weeklyHighestDate.set(priceDetails, initialDate);
      final Field monthlyHighestDate = PriceDetails.class.getDeclaredField("monthlyHighestDate");
      monthlyHighestDate.setAccessible(true);
      monthlyHighestDate.set(priceDetails, initialDate);

      // when
      priceDetails.addLastPrice(new Product(), newPrice);

      // then
      assertAll(() -> assertThat(priceDetails.getWeeklyHighestPrice()).isEqualTo(initialPrice),
          () -> assertThat(priceDetails.getWeeklyHighestDate()).isEqualTo(initialDate),
          () -> assertThat(priceDetails.getMonthlyHighestPrice()).isEqualTo(initialPrice),
          () -> assertThat(priceDetails.getMonthlyHighestDate()).isEqualTo(initialDate));
    }
  }

  @Nested
  @DisplayName("기간별 최저가 갱신 테스트")
  class updatePeriodicPricesTest {

    @Test
    @DisplayName("1주일 최저가 업데이트일이 1주일을 넘었을 경우 1주일 최저가를 업데이트한다.")
    void updateOutdatedWeeklyLowestTest() throws NoSuchFieldException, IllegalAccessException {
      // given
      final int initialPrice = 8_000;
      final int expectPrice = 9_000;
      final PriceDetails priceDetails = new PriceDetails(10_000, initialPrice);
      priceDetails.addLastPrice(new Product(), 9_000);

      final Field createdAt = PriceHistory.class.getDeclaredField("createdAt");
      createdAt.setAccessible(true);
      createdAt.set(priceDetails.getPriceHistories().get(0), LocalDateTime.now());

      final Field weeklyLowestDate = PriceDetails.class.getDeclaredField("weeklyLowestDate");
      weeklyLowestDate.setAccessible(true);
      weeklyLowestDate.set(priceDetails, LocalDate.now().minusDays(8));

      // when
      priceDetails.updateOutdatedPeriodicPrices();

      // then
      assertAll(
          () -> assertThat(priceDetails.getWeeklyLowestPrice()).isEqualTo(expectPrice),
          () -> assertThat(priceDetails.getWeeklyLowestDate()).isEqualTo(LocalDate.now())
      );
    }


    @Test
    @DisplayName("1주일 최저가 업데이트일이 1주일을 넘지 않았을 경우 1주일 최저가를 업데이트하지 않는다.")
    void notUpdateOutdatedWeeklyLowestTest() throws NoSuchFieldException, IllegalAccessException {
      // given
      final int expectPrice = 8_000;
      final LocalDate expectDate = LocalDate.now().minusDays(7);
      final PriceDetails priceDetails = new PriceDetails(10_000, expectPrice);
      priceDetails.addLastPrice(new Product(), 7_000);

      final Field createdAt = PriceHistory.class.getDeclaredField("createdAt");
      createdAt.setAccessible(true);
      createdAt.set(priceDetails.getPriceHistories().get(0), LocalDateTime.now());

      final Field weeklyLowestPrice = PriceDetails.class.getDeclaredField("weeklyLowestPrice");
      weeklyLowestPrice.setAccessible(true);
      weeklyLowestPrice.set(priceDetails, expectPrice);
      final Field weeklyLowestDate = PriceDetails.class.getDeclaredField("weeklyLowestDate");
      weeklyLowestDate.setAccessible(true);
      weeklyLowestDate.set(priceDetails, expectDate);

      // when
      priceDetails.updateOutdatedPeriodicPrices();

      // then
      assertAll(
          () -> assertThat(priceDetails.getWeeklyLowestPrice()).isEqualTo(expectPrice),
          () -> assertThat(priceDetails.getWeeklyLowestDate()).isEqualTo(expectDate)
      );
    }

    @Test
    @DisplayName("1주일 최저가 업데이트일이 1주일을 넘었을 경우 1주일 이내의 가격 중 최저가로 업데이트한다.")
    void updateWeeklyLowestPriceInWeekTest() throws NoSuchFieldException, IllegalAccessException {
      // given
      final int initialPrice = 8_000;
      final int expectPrice = 7_000;
      final LocalDate expectDate = LocalDate.now().minusDays(7);
      final PriceDetails priceDetails = new PriceDetails(10_000, initialPrice);
      priceDetails.addLastPrice(new Product(), 6_000);
      priceDetails.addLastPrice(new Product(), 7_000);
      priceDetails.addLastPrice(new Product(), 8_000);

      final Field createdAt = PriceHistory.class.getDeclaredField("createdAt");
      createdAt.setAccessible(true);
      createdAt.set(priceDetails.getPriceHistories().get(0), LocalDateTime.now().minusDays(8));
      createdAt.set(priceDetails.getPriceHistories().get(1), LocalDateTime.now().minusDays(7));
      createdAt.set(priceDetails.getPriceHistories().get(2), LocalDateTime.now().minusDays(6));

      final Field weeklyLowestDate = PriceDetails.class.getDeclaredField("weeklyLowestDate");
      weeklyLowestDate.setAccessible(true);
      weeklyLowestDate.set(priceDetails, LocalDate.now().minusDays(8));

      // when
      priceDetails.updateOutdatedPeriodicPrices();

      // then
      assertAll(
          () -> assertThat(priceDetails.getWeeklyLowestPrice()).isEqualTo(expectPrice),
          () -> assertThat(priceDetails.getWeeklyLowestDate()).isEqualTo(expectDate)
      );
    }

    @Test
    @DisplayName("1달 최저가 업데이트일이 1달을 넘었을 경우 1달 최저가를 업데이트한다.")
    void updateOutdatedMonthlyLowestTest() throws NoSuchFieldException, IllegalAccessException {
      // given
      final int initialPrice = 8_000;
      final int expectPrice = 9_000;
      final PriceDetails priceDetails = new PriceDetails(10_000, initialPrice);
      priceDetails.addLastPrice(new Product(), 9_000);

      final Field createdAt = PriceHistory.class.getDeclaredField("createdAt");
      createdAt.setAccessible(true);
      createdAt.set(priceDetails.getPriceHistories().get(0), LocalDateTime.now());

      final Field monthlyLowestDate = PriceDetails.class.getDeclaredField("monthlyLowestDate");
      monthlyLowestDate.setAccessible(true);
      monthlyLowestDate.set(priceDetails, LocalDate.now().minusDays(31));

      // when
      priceDetails.updateOutdatedPeriodicPrices();

      // then
      assertAll(
          () -> assertThat(priceDetails.getMonthlyLowestPrice()).isEqualTo(expectPrice),
          () -> assertThat(priceDetails.getMonthlyLowestDate()).isEqualTo(LocalDate.now())
      );
    }


    @Test
    @DisplayName("1달 최저가 업데이트일이 1달을 넘지 않았을 경우 1달 최저가를 업데이트하지 않는다.")
    void notUpdateOutdatedMonthlyLowestTest() throws NoSuchFieldException, IllegalAccessException {
      // given
      final int expectPrice = 8_000;
      final LocalDate expectDate = LocalDate.now().minusDays(30);
      final PriceDetails priceDetails = new PriceDetails(10_000, expectPrice);
      priceDetails.addLastPrice(new Product(), 7_000);

      final Field createdAt = PriceHistory.class.getDeclaredField("createdAt");
      createdAt.setAccessible(true);
      createdAt.set(priceDetails.getPriceHistories().get(0), LocalDateTime.now());

      final Field monthlyLowestPrice = PriceDetails.class.getDeclaredField("monthlyLowestPrice");
      monthlyLowestPrice.setAccessible(true);
      monthlyLowestPrice.set(priceDetails, expectPrice);
      final Field monthlyLowestDate = PriceDetails.class.getDeclaredField("monthlyLowestDate");
      monthlyLowestDate.setAccessible(true);
      monthlyLowestDate.set(priceDetails, expectDate);

      // when
      priceDetails.updateOutdatedPeriodicPrices();

      // then
      assertAll(
          () -> assertThat(priceDetails.getMonthlyLowestPrice()).isEqualTo(expectPrice),
          () -> assertThat(priceDetails.getMonthlyLowestDate()).isEqualTo(expectDate)
      );
    }

    @Test
    @DisplayName("1달 최저가 업데이트일이 1달을 넘었을 경우 1달 이내의 가격 중 최저가로 업데이트한다.")
    void updateMonthlyLowestPriceInWeekTest() throws NoSuchFieldException, IllegalAccessException {
      // given
      final int initialPrice = 8_000;
      final int expectPrice = 7_000;
      final LocalDate expectDate = LocalDate.now().minusDays(30);
      final PriceDetails priceDetails = new PriceDetails(10_000, initialPrice);
      priceDetails.addLastPrice(new Product(), 6_000);
      priceDetails.addLastPrice(new Product(), 7_000);
      priceDetails.addLastPrice(new Product(), 8_000);

      final Field createdAt = PriceHistory.class.getDeclaredField("createdAt");
      createdAt.setAccessible(true);
      createdAt.set(priceDetails.getPriceHistories().get(0), LocalDateTime.now().minusDays(31));
      createdAt.set(priceDetails.getPriceHistories().get(1), LocalDateTime.now().minusDays(30));
      createdAt.set(priceDetails.getPriceHistories().get(2), LocalDateTime.now().minusDays(29));

      final Field monthlyLowestDate = PriceDetails.class.getDeclaredField("monthlyLowestDate");
      monthlyLowestDate.setAccessible(true);
      monthlyLowestDate.set(priceDetails, LocalDate.now().minusDays(31));

      // when
      priceDetails.updateOutdatedPeriodicPrices();

      // then
      assertAll(
          () -> assertThat(priceDetails.getMonthlyLowestPrice()).isEqualTo(expectPrice),
          () -> assertThat(priceDetails.getMonthlyLowestDate()).isEqualTo(expectDate)
      );
    }
  }
}

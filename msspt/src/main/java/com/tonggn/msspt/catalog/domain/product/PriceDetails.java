package com.tonggn.msspt.catalog.domain.product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class PriceDetails {

  @Column(nullable = false, updatable = false)
  private Integer normalPrice;
  @Column(nullable = false)
  private Integer latestPrice;
  @Column(nullable = false)
  private Integer beforeLatestPrice;

  @Column(nullable = false)
  private Integer weeklyLowestPrice;
  @Column(nullable = false)
  private LocalDate weeklyLowestDate;

  @Column(nullable = false)
  private Integer weeklyHighestPrice;
  @Column(nullable = false)
  private LocalDate weeklyHighestDate;

  @Column(nullable = false)
  private Integer monthlyLowestPrice;
  @Column(nullable = false)
  private LocalDate monthlyLowestDate;

  @Column(nullable = false)
  private Integer monthlyHighestPrice;
  @Column(nullable = false)
  private LocalDate monthlyHighestDate;

  @Getter(AccessLevel.NONE)
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
      CascadeType.MERGE})
  private List<PriceHistory> priceHistories = new ArrayList<>();

  PriceDetails(final Integer normalPrice, final Integer currentPrice) {
    this.normalPrice = normalPrice;
    this.latestPrice = currentPrice;
    this.beforeLatestPrice = currentPrice;
    this.weeklyLowestPrice = currentPrice;
    this.weeklyLowestDate = LocalDate.now();
    this.weeklyHighestPrice = currentPrice;
    this.weeklyHighestDate = LocalDate.now();
    this.monthlyLowestPrice = currentPrice;
    this.monthlyLowestDate = LocalDate.now();
    this.monthlyHighestPrice = currentPrice;
    this.monthlyHighestDate = LocalDate.now();
  }

  List<PriceHistory> getPriceHistories() {
    return priceHistories.stream().toList();
  }

  void addLastPrice(final Product product, final int price) {
    updateLatestPrice(price);
    updateWeeklyPrice(price);
    updateMonthlyPrice(price);
    this.priceHistories.add(new PriceHistory(product, price));
  }

  private void updateLatestPrice(final int price) {
    this.beforeLatestPrice = this.latestPrice;
    this.latestPrice = price;
  }

  private void updateWeeklyPrice(final int price) {
    if (price < weeklyLowestPrice) {
      weeklyLowestPrice = price;
      weeklyLowestDate = LocalDate.now();
    }
    if (price > weeklyHighestPrice) {
      weeklyHighestPrice = price;
      weeklyHighestDate = LocalDate.now();
    }
  }

  private void updateMonthlyPrice(final int price) {
    if (price < monthlyLowestPrice) {
      monthlyLowestPrice = price;
      monthlyLowestDate = LocalDate.now();
    }
    if (price > monthlyHighestPrice) {
      monthlyHighestPrice = price;
      monthlyHighestDate = LocalDate.now();
    }
  }
}

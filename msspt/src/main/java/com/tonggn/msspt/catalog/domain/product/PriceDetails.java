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

  @Column(nullable = false)
  private Integer normalPrice;
  @Column(nullable = false)
  private Integer latestPrice;
  private Integer beforeLatestPrice;

  private Integer weeklyLowestPrice;
  private LocalDate weeklyLowestDate;

  private Integer weeklyHighestPrice;
  private LocalDate weeklyHighestDate;

  private Integer monthlyLowestPrice;
  private LocalDate monthlyLowestDate;

  private Integer monthlyHighestPrice;
  private LocalDate monthlyHighestDate;

  @Getter(AccessLevel.NONE)
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
      CascadeType.MERGE})
  private List<PriceHistory> priceHistories = new ArrayList<>();

  PriceDetails(final Integer initialPrice) {
    this.normalPrice = initialPrice;
    this.latestPrice = initialPrice;
  }

  List<PriceHistory> getPriceHistories() {
    return priceHistories.stream().toList();
  }

  void addLastPrice(final Product product, final int price) {
    this.beforeLatestPrice = this.latestPrice;
    this.latestPrice = price;
    this.priceHistories.add(new PriceHistory(product, price));
  }
}

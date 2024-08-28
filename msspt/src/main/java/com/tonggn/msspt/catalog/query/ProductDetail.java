package com.tonggn.msspt.catalog.query;

import java.util.List;

public record ProductDetail(
    long id,
    long goodsNo,
    String name,
    int normalPrice,
    String imageUrl,
    String brandName,
    List<Price> sortedPrices
) {

  @Override
  public List<Price> sortedPrices() {
    return sortedPrices.stream()
        .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
        .toList();
  }

  public void addPrice(final Price price) {
    sortedPrices.add(price);
  }

  public record Price(
      long id,
      int price,
      String createdAt
  ) {

  }
}

package com.tonggn.msspt.catalog.query;

public record PriceDropProductSummary(
    long id,
    long goodsNo,
    String name,
    int normalPrice,
    String imageUrl,
    String brandName,
    int latestPrice,
    int maxPrice
) {

}

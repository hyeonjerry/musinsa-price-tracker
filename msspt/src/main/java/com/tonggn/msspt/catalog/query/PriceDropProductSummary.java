package com.tonggn.msspt.catalog.query;

public record PriceDropProductSummary(
    long id,
    long goodsNo,
    String name,
    long normalPrice,
    String imageUrl,
    String brandName,
    long latestPrice,
    long maxPrice
) {

}

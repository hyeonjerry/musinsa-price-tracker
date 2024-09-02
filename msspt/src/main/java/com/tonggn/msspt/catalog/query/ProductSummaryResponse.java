package com.tonggn.msspt.catalog.query;

public record ProductSummaryResponse(
    long productId,
    long goodsNo,
    String name,
    int normalPrice,
    int latestPrice,
    int beforeLatestPrice,
    int weeklyLowestPrice,
    String weeklyLowestDate,
    int weeklyHighestPrice,
    String weeklyHighestDate,
    int monthlyLowestPrice,
    String monthlyLowestDate,
    int monthlyHighestPrice,
    String monthlyHighestDate,
    String imageUrl,
    String brandName
) {

}

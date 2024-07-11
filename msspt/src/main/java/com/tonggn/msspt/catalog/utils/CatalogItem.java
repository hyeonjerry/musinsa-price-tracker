package com.tonggn.msspt.catalog.utils;

public record CatalogItem(
    Long goodsNo,
    String goodsName,
    String imageUrl,
    int normalPrice,
    int price,
    String brand,
    String brandName,
    String brandNameEng
) {

}

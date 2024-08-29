package com.tonggn.msspt.catalog.application;

import com.tonggn.msspt.catalog.domain.category.CategoryId;
import com.tonggn.msspt.catalog.domain.product.GoodsNo;
import com.tonggn.msspt.catalog.utils.CatalogItem;

public record ProductUpdateRequest(
    GoodsNo goodsNo,
    String name,
    int normalPrice,
    int price,
    String imageUrl,
    String brandId,
    CategoryId categoryId
) {

  public static ProductUpdateRequest of(final CatalogItem item, final CategoryId categoryId) {
    return new ProductUpdateRequest(new GoodsNo(item.goodsNo()), item.goodsName(),
        item.normalPrice(), item.price(), item.imageUrl(), item.brand(), categoryId);
  }
}

package com.tonggn.msspt.catalog.command.application;

import com.tonggn.msspt.catalog.command.domain.brand.BrandId;
import com.tonggn.msspt.catalog.command.domain.category.CategoryId;
import com.tonggn.msspt.catalog.command.domain.product.GoodsNo;
import com.tonggn.msspt.catalog.utils.CatalogItem;

public record ProductUpdateRequest(
    GoodsNo goodsNo,
    String name,
    int normalPrice,
    int price,
    String imageUrl,
    BrandId brandId,
    CategoryId categoryId
) {

  public static ProductUpdateRequest of(final CatalogItem item, final CategoryId categoryId) {
    return new ProductUpdateRequest(new GoodsNo(item.goodsNo()), item.goodsName(),
        item.normalPrice(), item.price(), item.imageUrl(), new BrandId(item.brand()), categoryId);
  }
}

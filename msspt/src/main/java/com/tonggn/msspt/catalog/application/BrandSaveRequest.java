package com.tonggn.msspt.catalog.application;

import com.tonggn.msspt.catalog.utils.CatalogItem;

public record BrandSaveRequest(
    String id,
    String name,
    String englishName
) {

  public static BrandSaveRequest from(final CatalogItem item) {
    return new BrandSaveRequest(item.brand(), item.brandName(), item.brandNameEng());
  }
}

package com.tonggn.msspt.catalog.application;

import com.tonggn.msspt.catalog.domain.category.Category;

public record CategoryResponse(
    String id,
    String name
) {

  public static CategoryResponse from(final Category category) {
    return new CategoryResponse(category.getId().getValue(), category.getName());
  }
}

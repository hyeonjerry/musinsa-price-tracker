package com.tonggn.msspt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "mss.api-url")
public class MssApiUrlProperties {

  private final int catalogPageSize;
  private final String catalogByCategoryAndPageAndSizeUrl;

  public String getProductsUrl(final String categoryId, final int page) {
    return String.format(catalogByCategoryAndPageAndSizeUrl, categoryId, page, catalogPageSize);
  }

  public boolean isLastPage(final int productCount) {
    return productCount < catalogPageSize;
  }
}

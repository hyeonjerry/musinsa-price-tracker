package com.tonggn.msspt.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "mss.api-url")
public class MssApiUrlProperties {

  @Getter
  private final int pageSize;
  private final String productsByCategoryAndPageAndSizeUrl;

  public String getProductsUrl(final String categoryId, final int page) {
    return String.format(productsByCategoryAndPageAndSizeUrl, categoryId, page, pageSize);
  }
}

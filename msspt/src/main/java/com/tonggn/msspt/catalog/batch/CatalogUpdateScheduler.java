package com.tonggn.msspt.catalog.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tonggn.msspt.catalog.application.BrandSaveRequest;
import com.tonggn.msspt.catalog.application.BrandSaveService;
import com.tonggn.msspt.catalog.application.ProductUpdateRequest;
import com.tonggn.msspt.catalog.application.ProductUpdateService;
import com.tonggn.msspt.catalog.domain.category.Category;
import com.tonggn.msspt.catalog.domain.category.CategoryRepository;
import com.tonggn.msspt.catalog.utils.CatalogItem;
import com.tonggn.msspt.catalog.utils.CatalogParser;
import com.tonggn.msspt.catalog.utils.HttpClient;
import com.tonggn.msspt.catalog.utils.ProxyHttpClient;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogUpdateScheduler {

  private final CatalogParser catalogParser;
  private final CategoryRepository categoryRepository;
  private final BrandSaveService brandSaveService;
  private final ProductUpdateService productUpdateService;

  @Value("${mss.api-url}")
  private String apiUrl;
  // @Value("${mss.proxy.free-list-url}")
  // private String proxyListUrl;
  @Value("${mss.proxy.paid-host}")
  private String proxyHost;
  @Value("${mss.proxy.paid-port}")
  private int proxyPort;

  @Scheduled(cron = "0 0 2 * * *") // 초 분 시 일 월 요일
  public void update() {
    final HttpClient httpClient = new ProxyHttpClient(proxyHost, proxyPort);
    final List<Category> categories = categoryRepository.findAll();
    for (final Category category : categories) {
      updateCatalogByCategory(category, httpClient);
    }
  }

  private void updateCatalogByCategory(
      final Category category,
      final HttpClient httpClient
  ) {
    int page = 1;
    while (true) {
      final String categoryId = category.getId().getValue();
      final String url = getApiUrl(categoryId, page);
      final List<CatalogItem> items = fetchItemsByUrl(url, httpClient);
      saveItems(items, category);
      if (items.isEmpty()) {
        break;
      }
      page++;
    }
  }

  private String getApiUrl(final String categoryId, final int page) {
    return String.format(apiUrl, categoryId, page);
  }

  private List<CatalogItem> fetchItemsByUrl(final String url, final HttpClient httpClient) {
    log.info("Fetching products from {}", url);
    try {
      final String response = httpClient.get(url);
      return catalogParser.parse(response);
    } catch (final JsonProcessingException e) {
      log.error("Failed to parse products: {}", e.getMessage());
    }
    return List.of();
  }

  private void saveItems(final List<CatalogItem> items, final Category category) {
    final Set<BrandSaveRequest> brands = items.stream()
        .map(BrandSaveRequest::from)
        .collect(Collectors.toUnmodifiableSet());
    final List<ProductUpdateRequest> products = items.stream()
        .map(item -> ProductUpdateRequest.of(item, category.getId().getValue()))
        .toList();
    brandSaveService.saveOnlyNewBrands(brands);
    productUpdateService.saveOrUpdateProducts(products);
  }
}

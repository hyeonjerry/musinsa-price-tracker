package com.tonggn.msspt.catalog.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tonggn.msspt.catalog.application.BrandSaveRequest;
import com.tonggn.msspt.catalog.application.BrandSaveService;
import com.tonggn.msspt.catalog.application.CategoryQueryService;
import com.tonggn.msspt.catalog.application.CategoryResponse;
import com.tonggn.msspt.catalog.application.ProductUpdateRequest;
import com.tonggn.msspt.catalog.application.ProductUpdateService;
import com.tonggn.msspt.catalog.utils.CatalogItem;
import com.tonggn.msspt.catalog.utils.CatalogParser;
import com.tonggn.msspt.catalog.utils.HttpClient;
import com.tonggn.msspt.catalog.utils.ProxyHttpClient;
import com.tonggn.msspt.config.MssApiUrlProperties;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogUpdateScheduler {

  private final CatalogParser catalogParser;
  private final MssApiUrlProperties mssApiUrlProperties;
  private final CategoryQueryService categoryQueryService;
  private final BrandSaveService brandSaveService;
  private final ProductUpdateService productUpdateService;
  private final ProxyHttpClient httpClient;

  @Scheduled(cron = "0 0 0/6 * * *") // 초 분 시 일 월 요일
  public void update() {
    final List<CategoryResponse> categories = categoryQueryService.getCategories();
    for (final CategoryResponse category : categories) {
      updateCatalogByCategory(category);
    }
  }

  private void updateCatalogByCategory(final CategoryResponse category) {
    int page = 1;
    while (true) {
      final String url = mssApiUrlProperties.getProductsUrl(category.id(), page);
      final List<CatalogItem> items = fetchItemsByUrl(url, httpClient);
      saveItems(items, category);
      if (items.isEmpty()) {
        break;
      }
      page++;
    }
  }

  private List<CatalogItem> fetchItemsByUrl(final String url, final HttpClient httpClient) {
    log.info("Fetching products from {}", url);
    try {
      final String response = httpClient.get(url);
      return catalogParser.parse(response);
    } catch (final RestClientException e) {
      log.error("Failed to fetch products: {}", e.getMessage());
      return fetchItemsByUrl(url, httpClient);
    } catch (final JsonProcessingException e) {
      log.error("Failed to parse products: {}", e.getMessage());
    }
    return List.of();
  }

  private void saveItems(final List<CatalogItem> items, final CategoryResponse category) {
    final Set<BrandSaveRequest> brands = items.stream()
        .map(BrandSaveRequest::from)
        .collect(Collectors.toUnmodifiableSet());
    final List<ProductUpdateRequest> products = items.stream()
        .map(item -> ProductUpdateRequest.of(item, category.id()))
        .toList();
    brandSaveService.saveOnlyNewBrands(brands);
    productUpdateService.saveOrUpdateProducts(products);
  }
}

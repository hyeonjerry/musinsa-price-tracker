package com.tonggn.msspt.catalog.application;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import com.tonggn.msspt.catalog.domain.category.CategoryRepository;
import com.tonggn.msspt.catalog.domain.product.Product;
import com.tonggn.msspt.catalog.domain.product.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CatalogUpdateService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public List<CategoryResponse> getCategories() {
    return categoryRepository.findAll()
        .stream()
        .map(CategoryResponse::from)
        .toList();
  }

  @Transactional
  public void saveAndUpdateProducts(final List<ProductUpdateRequest> requests) {
    final List<Product> products = requests.stream()
        .map(request -> {
          final Product product = findOrCreateNewProduct(request);
          product.addLastPriceIfNew(request.price());
          return product;
        }).toList();
    productRepository.saveAll(products);
  }

  private Product mapToProduct(final ProductUpdateRequest request) {
    final BrandId brandId = new BrandId(request.brandId());
    final CategoryId categoryid = new CategoryId(request.categoryId());
    return new Product(request.goodsNo(), request.name(), request.normalPrice(),
        request.imageUrl(), brandId, categoryid);
  }

  private Product findOrCreateNewProduct(final ProductUpdateRequest request) {
    return productRepository.findByGoodsNo(request.goodsNo())
        .orElseGet(() -> mapToProduct(request));
  }
}

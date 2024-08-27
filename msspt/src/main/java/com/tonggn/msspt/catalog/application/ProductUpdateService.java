package com.tonggn.msspt.catalog.application;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import com.tonggn.msspt.catalog.domain.product.Product;
import com.tonggn.msspt.catalog.domain.product.ProductRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductUpdateService {

  private final ProductRepository productRepository;

  public void saveOrUpdateProducts(final List<ProductUpdateRequest> requests) {
    final List<Long> goodsNos = requests.stream()
        .map(ProductUpdateRequest::goodsNo)
        .toList();

    final Map<Long, Product> existingProducts = productRepository
        .findByGoodsNoIn(goodsNos).stream()
        .collect(Collectors.toMap(Product::getGoodsNo, product -> product));

    final List<Product> products = requests.stream()
        .map(request -> addPriceToProduct(request, existingProducts))
        .toList();

    productRepository.saveAll(products);
  }

  private Product addPriceToProduct(
      final ProductUpdateRequest request,
      final Map<Long, Product> existingProducts
  ) {
    final Product product = existingProducts.getOrDefault(request.goodsNo(), mapToProduct(request));
    product.addLastPrice(request.price());
    return product;
  }

  private Product mapToProduct(final ProductUpdateRequest request) {
    final BrandId brandId = new BrandId(request.brandId());
    final CategoryId categoryid = new CategoryId(request.categoryId());
    return new Product(request.goodsNo(), request.name(), request.normalPrice(),
        request.imageUrl(), brandId, categoryid);
  }
}

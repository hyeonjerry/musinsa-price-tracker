package com.tonggn.msspt.catalog.application;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.product.GoodsNo;
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
    final Map<GoodsNo, Product> existingProducts = fetchExistingProducts(requests);

    final List<Product> products = requests.stream()
        .map(request -> addPriceToProduct(request, existingProducts))
        .toList();

    productRepository.saveAll(products);
  }

  private Map<GoodsNo, Product> fetchExistingProducts(final List<ProductUpdateRequest> requests) {
    final List<GoodsNo> goodsNos = requests.stream()
        .map(ProductUpdateRequest::goodsNo)
        .toList();

    return productRepository.findByGoodsNoIn(goodsNos).stream()
        .collect(Collectors.toMap(Product::getGoodsNo, product -> product));
  }

  private Product addPriceToProduct(
      final ProductUpdateRequest request,
      final Map<GoodsNo, Product> existingProducts
  ) {
    final Product product = existingProducts.getOrDefault(request.goodsNo(), mapToProduct(request));
    product.addLastPrice(request.price());
    return product;
  }

  private Product mapToProduct(final ProductUpdateRequest request) {
    final BrandId brandId = new BrandId(request.brandId());
    return new Product(request.goodsNo(), request.name(), request.normalPrice(),
        request.imageUrl(), brandId, request.categoryId());
  }
}

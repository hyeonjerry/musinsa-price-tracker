package com.tonggn.msspt.catalog.command.application;

import com.tonggn.msspt.catalog.command.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PriceDetailUpdateService {

  private final ProductRepository productRepository;

  public long getMinimumProductId() {
    return productRepository.findMinimumProductId();
  }

  public long getMaximumProductId() {
    return productRepository.findMaximumProductId();
  }

  public void updateOutdatedPrices(final long startProductId, final long endProductId) {
    productRepository.findProductsToUpdatePriceDetails(startProductId, endProductId)
        .forEach(product -> product.getPriceDetails().updateOutdatedPeriodicPrices());
  }
}

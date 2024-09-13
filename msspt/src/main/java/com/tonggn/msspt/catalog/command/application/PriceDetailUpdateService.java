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

  public void updateOutdatedPrices() {
    productRepository.findProductsToUpdatePriceDetails()
        .forEach(product -> product.getPriceDetails().updateOutdatedPeriodicPrices());
  }
}

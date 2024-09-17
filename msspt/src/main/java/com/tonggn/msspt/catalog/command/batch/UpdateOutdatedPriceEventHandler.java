package com.tonggn.msspt.catalog.command.batch;

import com.tonggn.msspt.catalog.command.application.PriceDetailUpdateService;
import com.tonggn.msspt.catalog.utils.CatalogUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateOutdatedPriceEventHandler {

  private static final int PRODUCTS_SIZE = 10_000;

  private final PriceDetailUpdateService priceDetailUpdateService;

  @EventListener(CatalogUpdatedEvent.class)
  public void handle(final CatalogUpdatedEvent event) {
    final long minProductId = priceDetailUpdateService.getMinimumProductId();
    final long maxProductId = priceDetailUpdateService.getMaximumProductId();
    for (long i = minProductId; i <= maxProductId; i += PRODUCTS_SIZE) {
      final long endProductId = i + PRODUCTS_SIZE - 1;
      priceDetailUpdateService.updateOutdatedPrices(i, endProductId);
    }
  }
}

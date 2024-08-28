package com.tonggn.msspt.catalog.ui;

import com.tonggn.msspt.catalog.query.PriceDropProductSummary;
import com.tonggn.msspt.catalog.query.ProductDao;
import com.tonggn.msspt.catalog.query.ProductDetail;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApi {

  private static final int PAGE_SIZE = 30;

  private final ProductDao productDao;

  @GetMapping("/search")
  public List<ProductDetail> search(
      @RequestParam final String keyword,
      @RequestParam(defaultValue = "1") final int page
  ) {
    final int offset = (page - 1) * PAGE_SIZE;
    return productDao.findByNameContaining(keyword, PAGE_SIZE, offset);
  }

  @GetMapping("/price-drop")
  public List<PriceDropProductSummary> priceDrop(@RequestParam(defaultValue = "1") final int page) {
    final int offset = (page - 1) * PAGE_SIZE;
    return productDao.findPriceDropProducts(PAGE_SIZE, offset);
  }
}

package com.tonggn.msspt.catalog.command.domain.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByGoodsNoIn(List<GoodsNo> goodsNos);

  @Query("""
      select p
      from Product p
      join fetch p.priceDetails.priceHistories ph
      where (p.priceDetails.weeklyLowestDate < subdate(current_date, 7)
      or p.priceDetails.weeklyHighestDate < subdate(current_date, 7)
      or p.priceDetails.monthlyLowestDate < subdate(current_date, 30)
      or p.priceDetails.monthlyHighestDate < subdate(current_date, 30))
      and ph.createdAt > subdate(current_date, 30)
      """)
  List<Product> findProductsToUpdatePriceDetails();
}

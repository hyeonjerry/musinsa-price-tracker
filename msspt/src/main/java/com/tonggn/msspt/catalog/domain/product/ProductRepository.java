package com.tonggn.msspt.catalog.domain.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("""
            SELECT p
            FROM Product p
            INNER JOIN FETCH p.priceHistories ph
            WHERE p.goodsNo IN :goodsNos
            ORDER BY ph.createdAt ASC
      """)
  List<Product> findByGoodsNoInWithPriceHistories(List<Long> goodsNos);
}

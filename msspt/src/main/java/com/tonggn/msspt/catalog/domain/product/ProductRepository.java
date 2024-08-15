package com.tonggn.msspt.catalog.domain.product;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("""
            SELECT p
            FROM Product p
            INNER JOIN FETCH p.priceHistories ph
            WHERE p.goodsNo = :goodsNo
            ORDER BY ph.createdAt ASC
      """)
  Optional<Product> findByGoodsNoWithPriceHistories(long goodsNo);
}

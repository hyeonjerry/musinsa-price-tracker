package com.tonggn.msspt.catalog.query;

import com.tonggn.msspt.catalog.query.ProductDetail.Price;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductDao {

  private final NamedParameterJdbcTemplate namedJdbc;

  public List<ProductDetail> findByNameContaining(
      final String keyword,
      final int limit,
      final int offset
  ) {
    final String sql = """
        SELECT p.id,
                 p.goods_no,
                 p.name,
                 p.normal_price,
                 p.image_url,
                 p.brand_name,
                 ph.id AS price_history_id,
                 ph.price,
                 ph.created_at
          FROM (SELECT p.id,
                       p.goods_no,
                       p.name,
                       p.normal_price,
                       p.image_url,
                       b.name AS brand_name
                FROM product p
                         JOIN brand b ON p.brand_id = b.brand_id
                WHERE p.name LIKE :keyword
                LIMIT :limit OFFSET :offset) p
                   JOIN price_history ph ON p.id = ph.product_id
          """;

    final Map<String, Object> params = Map.of(
        "keyword", "%" + keyword + "%",
        "limit", limit,
        "offset", offset
    );

    final ResultSetExtractor<List<ProductDetail>> productDetailExtractor = rs -> {
      final HashMap<Long, ProductDetail> products = new HashMap<>();
      while (rs.next()) {
        final long productId = rs.getLong("id");
        final long goods_no = rs.getLong("goods_no");
        final String name = rs.getString("name");
        final int normal_price = rs.getInt("normal_price");
        final String image_url = rs.getString("image_url");
        final String brand_name = rs.getString("brand_name");
        final long priceHistoryId = rs.getLong("price_history_id");
        final long price = rs.getLong("price");
        final String createdAt = rs.getString("created_at");

        final ProductDetail product = products.computeIfAbsent(productId, k -> new ProductDetail(
            productId,
            goods_no,
            name,
            normal_price,
            image_url,
            brand_name,
            new ArrayList<>()
        ));
        product.addPrice(new Price(priceHistoryId, price, createdAt));
      }
      return products.values().stream().toList();
    };

    return namedJdbc.query(sql, params, productDetailExtractor);
  }
}

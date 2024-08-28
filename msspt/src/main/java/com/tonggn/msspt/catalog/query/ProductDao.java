package com.tonggn.msspt.catalog.query;

import com.tonggn.msspt.catalog.query.ProductDetail.Price;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
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

  public List<PriceDropProductSummary> findPriceDropProducts(final int pageSize, final int offset) {
    final String sql = """
        SELECT *
        FROM (SELECT p.id,
                     p.goods_no,
                     p.name,
                     p.normal_price,
                     p.image_url,
                     b.name                                 AS brand_name,
                     ph_latest.price                        AS latest_price,
                     MAX(ph_week.price)                     AS max_price,
                     (ph_latest.price - MAX(ph_week.price)) AS price_diff
              FROM product p
                       JOIN brand b ON p.brand_id = b.brand_id
                       JOIN price_history ph_latest ON p.id = ph_latest.product_id
                       JOIN price_history ph_week ON p.id = ph_week.product_id
              WHERE ph_latest.created_at = (SELECT MAX(ph.created_at)
                                            FROM price_history ph
                                            WHERE ph.product_id = p.id)
                AND ph_week.created_at >= NOW() - INTERVAL 1 WEEK
              GROUP BY p.id, p.goods_no, p.name, p.normal_price, p.image_url, b.name, ph_latest.price
              ORDER BY price_diff
              LIMIT :limit OFFSET :offset) as price_drop_table
        where price_diff < 0
        """;

    final Map<String, Object> params = Map.of("limit", pageSize, "offset", offset);

    final RowMapper<PriceDropProductSummary> priceDropProductSummaryRowMapper = (rs, rowNum) -> {
      final long id = rs.getLong("id");
      final long goodsNo = rs.getLong("goods_no");
      final String name = rs.getString("name");
      final long normalPrice = rs.getLong("normal_price");
      final String imageUrl = rs.getString("image_url");
      final String brandName = rs.getString("brand_name");
      final long latestPrice = rs.getLong("latest_price");
      final long maxPrice = rs.getLong("max_price");

      return new PriceDropProductSummary(id, goodsNo, name, normalPrice, imageUrl, brandName,
          latestPrice, maxPrice);
    };

    return namedJdbc.query(sql, params, priceDropProductSummaryRowMapper);
  }
}

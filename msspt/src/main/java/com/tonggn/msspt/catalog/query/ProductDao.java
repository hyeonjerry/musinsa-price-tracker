package com.tonggn.msspt.catalog.query;

import com.tonggn.msspt.catalog.query.ProductDetail.Price;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductDao {

  public static final RowMapper<ProductSummaryResponse> PRODUCT_SUMMARY_ROW_MAPPER = (rs, rowNum) -> {
    final long id = rs.getLong("id");
    final long goodsNo = rs.getLong("goods_no");
    final String name = rs.getString("name");
    final int normalPrice = rs.getInt("normal_price");
    final int latestPrice = rs.getInt("latest_price");
    final int beforeLatestPrice = rs.getInt("before_latest_price");
    final int weeklyLowestPrice = rs.getInt("weekly_lowest_price");
    final String weeklyLowestDate = rs.getString("weekly_lowest_date");
    final int weeklyHighestPrice = rs.getInt("weekly_highest_price");
    final String weeklyHighestDate = rs.getString("weekly_highest_date");
    final int monthlyLowestPrice = rs.getInt("monthly_lowest_price");
    final String monthlyLowestDate = rs.getString("monthly_lowest_date");
    final int monthlyHighestPrice = rs.getInt("monthly_highest_price");
    final String monthlyHighestDate = rs.getString("monthly_highest_date");
    final String imageUrl = rs.getString("image_url");
    final String brandName = rs.getString("brand_name");

    return new ProductSummaryResponse(id, goodsNo, name, normalPrice, latestPrice,
        beforeLatestPrice, weeklyLowestPrice, weeklyLowestDate, weeklyHighestPrice,
        weeklyHighestDate, monthlyLowestPrice, monthlyLowestDate, monthlyHighestPrice,
        monthlyHighestDate, imageUrl, brandName);
  };

  private final NamedParameterJdbcTemplate namedJdbc;

  @Value("${mss.product-url}")
  private String productUrlPrefix;

  @SneakyThrows(SQLException.class)
  private ProductDetail mapToProductDetail(final ResultSet rs) {
    final long productId = rs.getLong("id");
    final long goodsNo = rs.getLong("goods_no");
    final String productUrl = productUrlPrefix + "/" + goodsNo;
    final String name = rs.getString("name");
    final int normalPrice = rs.getInt("normal_price");
    final int latestPrice = rs.getInt("latest_price");
    final int beforeLatestPrice = rs.getInt("before_latest_price");
    final int weeklyLowestPrice = rs.getInt("weekly_lowest_price");
    final int weeklyHighestPrice = rs.getInt("weekly_highest_price");
    final int monthlyLowestPrice = rs.getInt("monthly_lowest_price");
    final int monthlyHighestPrice = rs.getInt("monthly_highest_price");
    final String imageUrl = rs.getString("image_url");
    final String brandName = rs.getString("brand_name");

    return new ProductDetail(
        productId,
        goodsNo,
        productUrl,
        name,
        normalPrice,
        latestPrice,
        beforeLatestPrice,
        weeklyLowestPrice,
        weeklyHighestPrice,
        monthlyLowestPrice,
        monthlyHighestPrice,
        imageUrl,
        brandName,
        new ArrayList<>()
    );
  }

  @SneakyThrows(SQLException.class)
  private void addPriceToProductDetail(final ResultSet rs, final ProductDetail product) {
    final long priceHistoryId = rs.getLong("price_history_id");
    final int price = rs.getInt("price");
    final String createdAt = rs.getString("created_at");
    product.addPrice(new Price(priceHistoryId, price, createdAt));
  }

  public List<ProductSummaryResponse> findByNameContaining(
      final String keyword,
      final int limit,
      final int offset
  ) {
    final String sql = """
        SELECT p.id,
               p.goods_no,
               p.name,
               p.normal_price,
               p.latest_price,
               p.before_latest_price,
               p.weekly_lowest_price,
               p.weekly_lowest_date,
               p.weekly_highest_price,
               p.weekly_highest_date,
               p.monthly_lowest_price,
               p.monthly_lowest_date,
               p.monthly_highest_price,
               p.monthly_highest_date,
               p.image_url,
               b.name AS brand_name
        FROM product p
                 JOIN brand b ON p.brand_id = b.brand_id
        WHERE p.name LIKE :keyword
        LIMIT :limit OFFSET :offset;
        """;

    final Map<String, Object> params = Map.of(
        "keyword", "%" + keyword + "%",
        "limit", limit,
        "offset", offset
    );

    return namedJdbc.query(sql, params, PRODUCT_SUMMARY_ROW_MAPPER);
  }

  public List<ProductSummaryResponse> findPeriodPriceDropProducts(
      final int pageSize,
      final int offset,
      final PeriodRequest period
  ) {
    final String priceDropRateColumn = getPriceDropRateColumnByPeriod(period);
    final String sql = """
        SELECT p.id,
               p.goods_no,
               p.name,
               p.normal_price,
               p.latest_price,
               p.before_latest_price,
               p.weekly_lowest_price,
               p.weekly_lowest_date,
               p.weekly_highest_price,
               p.weekly_highest_date,
               p.monthly_lowest_price,
               p.monthly_lowest_date,
               p.monthly_highest_price,
               p.monthly_highest_date,
               p.image_url,
               b.name AS brand_name,
               %1$s AS drop_rate
        FROM product p
                 JOIN brand b ON p.brand_id = b.brand_id
        WHERE %1$s < 0
        ORDER BY %1$s
        LIMIT :limit OFFSET :offset;
        """.formatted(priceDropRateColumn);

    final Map<String, Object> params = Map.of("limit", pageSize, "offset", offset);

    return namedJdbc.query(sql, params, PRODUCT_SUMMARY_ROW_MAPPER);
  }

  private String getPriceDropRateColumnByPeriod(final PeriodRequest period) {
    return switch (period) {
      case DAILY -> "p.daily_price_drop_rate";
      case WEEKLY -> "p.weekly_price_drop_rate";
      case MONTHLY -> "p.monthly_price_drop_rate";
    };
  }

  public ProductDetail findById(final long id) {
    final String sql = """
        SELECT p.id,
               p.goods_no,
               p.name,
               p.normal_price,
               p.latest_price,
               p.before_latest_price,
               p.weekly_lowest_price,
               p.weekly_highest_price,
               p.monthly_lowest_price,
               p.monthly_highest_price,
               p.image_url,
               b.name as brand_name,
               ph.id AS price_history_id,
               ph.price,
               ph.created_at
        FROM product p
              JOIN brand b ON p.brand_id = b.brand_id
              JOIN price_history ph ON p.id = ph.product_id
        WHERE p.id = :id
        """;

    final Map<String, Object> params = Map.of("id", id);

    final ResultSetExtractor<ProductDetail> productDetailExtractor = rs -> {
      rs.next();
      final ProductDetail product = mapToProductDetail(rs);
      do {
        addPriceToProductDetail(rs, product);
      } while (rs.next());

      return product;
    };

    return namedJdbc.query(sql, params, productDetailExtractor);
  }
}

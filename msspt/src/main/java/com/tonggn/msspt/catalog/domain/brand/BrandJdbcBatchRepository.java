package com.tonggn.msspt.catalog.domain.brand;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BrandJdbcBatchRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public void saveAll(final List<Brand> brands) {
    final String sql = "INSERT INTO brand (brand_id, name, english_name, created_at, updated_at) VALUES (:id, :name, :englishName, now(), now())";
    final Map[] values = brands.stream()
        .map(brand -> Map.of("id", brand.getId().getValue(), "name", brand.getName(), "englishName",
            brand.getEnglishName()))
        .toArray(Map[]::new);
    jdbcTemplate.batchUpdate(sql, values);
  }
}

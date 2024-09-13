package com.tonggn.msspt.catalog.command.domain.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(value = "classpath:clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class BrandJdbcBatchRepositoryTest {

  @Autowired
  private BrandRepository brandRepository;
  @Autowired
  private BrandJdbcBatchRepository brandBatchRepository;

  @Test
  @DisplayName("브랜드를 일괄 저장한다.")
  void saveAllTest() {
    // given
    final List<Brand> expects = List.of(
        new Brand(new BrandId("brand1"), "name1"),
        new Brand(new BrandId("brand2"), "name2"),
        new Brand(new BrandId("brand3"), "name3")
    );

    // when
    brandBatchRepository.saveAll(expects);

    // then
    final List<Brand> actual = brandRepository.findAll();
    assertThat(actual).usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expects);
  }
}

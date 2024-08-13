package com.tonggn.msspt.catalog.domain.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = "classpath:clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class BrandRepositoryTest {

  @Autowired
  private BrandRepository brandRepository;

  @Test
  @DisplayName("BrandId 리스트로 저장된 브랜드 목록을 조회한다.")
  void findAllByIdInTest() {
    // given
    final Brand brand1 = new Brand(new BrandId("brand1"), "name1", "englishName1");
    final Brand brand2 = new Brand(new BrandId("brand2"), "name2", "englishName2");
    final Brand brand3 = new Brand(new BrandId("brand3"), "name3", "englishName3");

    brandRepository.saveAll(List.of(brand1, brand2, brand3));

    // when
    final List<Brand> expects = List.of(brand2, brand3);
    final List<Brand> actual = brandRepository.findAllByIdIn(
        List.of(brand2.getId(), brand3.getId(), new BrandId("not exist"))
    );

    // then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expects);
  }
}

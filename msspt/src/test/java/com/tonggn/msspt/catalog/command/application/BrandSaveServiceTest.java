package com.tonggn.msspt.catalog.command.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.command.domain.brand.Brand;
import com.tonggn.msspt.catalog.command.domain.brand.BrandId;
import com.tonggn.msspt.catalog.command.domain.brand.BrandRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = "classpath:clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class BrandSaveServiceTest {

  @Autowired
  private BrandRepository brandRepository;
  @Autowired
  private BrandSaveService brandSaveService;

  @Test
  @DisplayName("새로운 브랜드를 저장한다.")
  void saveOnlyNewBrandsWithNewTest() {
    // given
    final List<Brand> expect = List.of(
        new Brand(new BrandId("A"), "브랜드A"),
        new Brand(new BrandId("B"), "브랜드B"),
        new Brand(new BrandId("C"), "브랜드C")
    );

    // when
    final List<BrandSaveRequest> requests = expect.stream()
        .map(this::mapToBrand)
        .toList();
    brandSaveService.saveOnlyNewBrands(requests);

    // then
    final List<Brand> actual = brandRepository.findAll();
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("이미 존재하는 브랜드는 저장하지 않는다.")
  void saveOnlyNewBrandsWithExistsTest() {
    // given
    final List<Brand> expect = List.of(
        new Brand(new BrandId("A"), "브랜드A"),
        new Brand(new BrandId("B"), "브랜드B"),
        new Brand(new BrandId("C"), "브랜드C")
    );
    brandRepository.saveAll(expect);

    // when
    final List<BrandSaveRequest> requests = expect.stream()
        .map(this::mapToBrand)
        .toList();
    brandSaveService.saveOnlyNewBrands(requests);

    // then
    final List<Brand> actual = brandRepository.findAll();
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expect);
  }

  private BrandSaveRequest mapToBrand(final Brand brand) {
    return new BrandSaveRequest(brand.getId().getValue(), brand.getName());
  }
}

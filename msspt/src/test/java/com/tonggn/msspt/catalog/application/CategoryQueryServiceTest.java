package com.tonggn.msspt.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.catalog.domain.category.Category;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import com.tonggn.msspt.catalog.domain.category.CategoryRepository;
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
class CategoryQueryServiceTest {

  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private CategoryQueryService categoryQueryService;

  @Test
  @DisplayName("상품 목록을 업데이트할 카테고리 목록을 조회한다.")
  void getCategoriesTest() {
    // given
    final List<CategoryResponse> expect = List.of(
        new CategoryResponse("A", "카테고리A"),
        new CategoryResponse("B", "카테고리B")
    );

    categoryRepository.saveAll(List.of(
        new Category(new CategoryId("A"), "카테고리A"),
        new Category(new CategoryId("B"), "카테고리B")
    ));

    // when
    final List<CategoryResponse> actual = categoryQueryService.getCategories();

    // then
    assertThat(actual).isEqualTo(expect);
  }
}

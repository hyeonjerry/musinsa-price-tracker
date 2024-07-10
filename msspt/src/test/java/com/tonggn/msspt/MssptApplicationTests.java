package com.tonggn.msspt;

import static org.assertj.core.api.Assertions.assertThat;

import com.tonggn.msspt.config.MssApiUrlProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MssptApplicationTests {

  @Test
  void contextLoads() {
  }

  @Nested
  class ConfigurationPropertiesTests {

    @Autowired
    private MssApiUrlProperties mssApiUrlProperties;

    @Value("${mss.api-url.catalog-page-size}")
    private int pageSize;
    @Value("${mss.api-url.catalog-by-category-and-page-and-size-url}")
    private String productsByCategoryUrl;

    @ParameterizedTest
    @ValueSource(strings = {"A", "002", " "})
    @DisplayName("categoryId에 해당하는 상품 목록 URL을 반환한다.")
    void getProductsByCategoryUrlTest(final String categoryId) {
      // given
      final int page = 1;
      final String expected = String.format(productsByCategoryUrl, categoryId, page, pageSize);
      // when
      final String actual = mssApiUrlProperties.getProductsUrl(categoryId, page);
      // then
      assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("page에 해당하는 상품 목록 URL을 반환한다.")
    void getProductsUrlByPage(final int page) {
      // given
      final String categoryId = "A";
      final String expected = String.format(productsByCategoryUrl, categoryId, page, pageSize);
      // when
      final String actual = mssApiUrlProperties.getProductsUrl(categoryId, page);
      // then
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("pageSize를 반환한다.")
    void getPageSizeTest() {
      // when
      final int actual = mssApiUrlProperties.getCatalogPageSize();
      // then
      assertThat(actual).isEqualTo(pageSize);
    }
  }
}

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
    private String productsUrl;
    @Value("${mss.api-url.request-delay-millis}")
    private int delayMillis;

    @ParameterizedTest
    @ValueSource(strings = {"A", "002", " "})
    @DisplayName("categoryId에 해당하는 상품 목록 URL을 반환한다.")
    void getProductsByCategoryUrlTest(final String categoryId) {
      // given
      final int page = 1;
      final String expected = String.format(productsUrl, categoryId, page, pageSize);
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
      final String expected = String.format(productsUrl, categoryId, page, pageSize);
      // when
      final String actual = mssApiUrlProperties.getProductsUrl(categoryId, page);
      // then
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("요청 지연 시간 테스트")
    void getRequestDelayMillisTest() {
      // given
      final int expected = this.delayMillis;
      // when
      final int actual = mssApiUrlProperties.getRequestDelayMillis();
      // then
      assertThat(actual).isEqualTo(expected);
    }
  }
}

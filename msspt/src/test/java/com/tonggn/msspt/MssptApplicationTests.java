package com.tonggn.msspt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MssptApplicationTests {

  @Value("${mss.api-url}")
  private String apiUrl;
  @Value("${mss.proxy.free-list-url}")
  private String proxyListUrl;
  @Value("${mss.proxy.paid-host}")
  private String proxyHost;
  @Value("${mss.proxy.paid-port}")
  private int proxyPort;

  @Test
  @DisplayName("API URL 환경변수 값 주입을 테스트한다.")
  void apiUrlPropertiesTest() {
    final String actual = String.format(apiUrl, "categoryId", 1);
    assertThat(actual).isEqualTo("https://api.url?category=categoryId&page=1");
  }

  @Test
  @DisplayName("프록시 환경변수 값 주입을 테스트한다.")
  void proxyPropertiesTest() {
    assertThat(proxyListUrl).isEqualTo("https://proxy.list.url");
    assertThat(proxyHost).isEqualTo("https://paid.proxy.host");
    assertThat(proxyPort).isEqualTo(1234);
  }
}

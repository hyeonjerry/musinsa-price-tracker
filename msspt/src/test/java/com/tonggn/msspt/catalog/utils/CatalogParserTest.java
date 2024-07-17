package com.tonggn.msspt.catalog.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

@JsonTest
class CatalogParserTest {

  @Autowired
  private ObjectMapper mapper;

  private CatalogParser parser;

  @BeforeEach
  void setUp() {
    parser = new CatalogParser(mapper);
  }

  @Test
  @DisplayName("parse 메서드는 JSON 문자열을 CatalogItem 리스트로 변환한다.")
  void parseTest() throws JsonProcessingException {
    // given
    final List<CatalogItem> expect = List.of(
        new CatalogItem(1L, "name", "url", 10000, 9000,
            "brand", "brandName", "brandNameEng"),
        new CatalogItem(2L, "name2", "url2", 20000, 19000,
            "brand2", "brandName2", "brandNameEng2")
    );

    // when
    final List<CatalogItem> actual = parser.parse("""
        {
          "data": {
            "goodsList": [
              {
                "goodsNo": 1,
                "goodsName": "name",
                "imageUrl": "url",
                "normalPrice": 10000,
                "price": 9000,
                "brand": "brand",
                "brandName": "brandName",
                "brandNameEng": "brandNameEng"
              },
              {
                "goodsNo": 2,
                "goodsName": "name2",
                "imageUrl": "url2",
                "normalPrice": 20000,
                "price": 19000,
                "brand": "brand2",
                "brandName": "brandName2",
                "brandNameEng": "brandNameEng2"
              }
            ]
          }
        }
        """);

    // then
    assertThat(actual).isEqualTo(expect);
  }
}

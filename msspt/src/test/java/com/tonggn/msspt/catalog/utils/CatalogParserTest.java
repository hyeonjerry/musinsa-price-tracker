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
        new CatalogItem(1L, "name1", "url1", 10_000, 9_000, "brand1", "brandName1"),
        new CatalogItem(2L, "name2", "url2", 20_000, 19_000, "brand2", "brandName2")
    );

    // when
    final List<CatalogItem> actual = parser.parse("""
        {
           "data": {
             "list": [
               {
                 "goodsNo": 1,
                 "goodsName": "name1",
                 "thumbnail": "url1",
                 "normalPrice": 10000,
                 "price": 9000,
                 "brand": "brand1",
                 "brandName": "brandName1"
               },
                {
                  "goodsNo": 2,
                  "goodsName": "name2",
                  "thumbnail": "url2",
                  "normalPrice": 20000,
                  "price": 19000,
                  "brand": "brand2",
                  "brandName": "brandName2"
                }
             ]
           }
        }
        """);

    // then
    assertThat(actual).isEqualTo(expect);
  }
}

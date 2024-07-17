package com.tonggn.msspt.catalog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogParser {

  private final ObjectMapper mapper;

  public List<CatalogItem> parse(final String json) throws JsonProcessingException {
    final JsonNode tree = mapper.readTree(json);
    final JsonNode goodsList = tree.path("data").path("goodsList");
    return StreamSupport.stream(goodsList.spliterator(), false)
        .map(this::mapToItem)
        .toList();
  }

  private CatalogItem mapToItem(final JsonNode item) {
    return new CatalogItem(
        item.path("goodsNo").asLong(),
        item.path("goodsName").asText(),
        item.path("imageUrl").asText(),
        item.path("normalPrice").asInt(),
        item.path("price").asInt(),
        item.path("brand").asText(),
        item.path("brandName").asText(),
        item.path("brandNameEng").asText()
    );
  }
}

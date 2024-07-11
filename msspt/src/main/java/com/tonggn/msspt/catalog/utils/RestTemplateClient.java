package com.tonggn.msspt.catalog.utils;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateClient {

  private final RestTemplate restTemplate;

  public RestTemplateClient(final RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public ResponseEntity<String> getForEntity(final String url) {
    return restTemplate.getForEntity(url, String.class);
  }
}

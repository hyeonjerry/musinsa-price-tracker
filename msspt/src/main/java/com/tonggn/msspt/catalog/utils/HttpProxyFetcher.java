package com.tonggn.msspt.catalog.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpProxyFetcher implements ProxyFetcher {

  private final String proxyListUrl;
  private final RestTemplate restTemplate;

  public HttpProxyFetcher(final String proxyListUrl) {
    this.proxyListUrl = proxyListUrl;
    this.restTemplate = new RestTemplate();
  }

  @Override
  public List<Proxy> fetch() {
    final List<String> rows = requestProxyList();
    return rows.stream()
        .map(this::mapToProxy)
        .toList();
  }

  private List<String> requestProxyList() {
    final ResponseEntity<String> response = restTemplate.getForEntity(proxyListUrl, String.class);
    return Arrays.stream(response.getBody().split("\n"))
        .toList();
  }

  private Proxy mapToProxy(final String row) {
    final var parts = row.split(":");
    final var host = parts[0];
    final var port = Integer.parseInt(parts[1]);
    return new Proxy(Type.HTTP, new InetSocketAddress(host, port));
  }
}

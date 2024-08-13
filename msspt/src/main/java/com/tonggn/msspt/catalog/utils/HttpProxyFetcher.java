package com.tonggn.msspt.catalog.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpProxyFetcher {

  private HttpProxyFetcher() {
  }

  public static List<Proxy> fetch(final String proxyListUrl) {
    final var rows = requestProxyList(proxyListUrl);
    return rows.stream()
        .map(HttpProxyFetcher::mapToProxy)
        .toList();
  }

  private static List<String> requestProxyList(final String proxyListUrl) {
    final RestTemplate restTemplate = new RestTemplate();
    final ResponseEntity<String> response = restTemplate.getForEntity(proxyListUrl, String.class);
    return Arrays.stream(response.getBody().split("\n"))
        .toList();
  }

  private static Proxy mapToProxy(final String row) {
    final var parts = row.split(":");
    final var host = parts[0];
    final var port = Integer.parseInt(parts[1]);
    return new Proxy(Type.HTTP, new InetSocketAddress(host, port));
  }
}

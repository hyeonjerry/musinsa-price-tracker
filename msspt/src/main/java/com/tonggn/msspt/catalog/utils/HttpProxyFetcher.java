package com.tonggn.msspt.catalog.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class HttpProxyFetcher {

  private HttpProxyFetcher() {
  }

  public static List<Proxy> fetchProxies(final String url) {
    final RestTemplate restTemplate = new RestTemplate();
    final String[] addresses = restTemplate.getForObject(url, String.class).split("\n");
    return Arrays.stream(addresses)
        .map(HttpProxyFetcher::mapToProxy)
        .toList();
  }

  private static Proxy mapToProxy(final String address) {
    final String[] parts = address.split(":");
    final String host = parts[0];
    final int port = Integer.parseInt(parts[1]);
    return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
  }
}

package com.tonggn.msspt.catalog.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ProxyHttpClient implements HttpClient {

  private static final int TIMEOUT_MILLIS = 10_000;
  private final RestTemplate restTemplate;

  public ProxyHttpClient(final String proxyHost, final int proxyPort) {
    final Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
    final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    // requestFactory.setProxy(proxy);
    requestFactory.setReadTimeout(TIMEOUT_MILLIS);
    requestFactory.setConnectTimeout(TIMEOUT_MILLIS);
    this.restTemplate = new RestTemplate(requestFactory);
    this.restTemplate.getInterceptors()
        .add(((request, body, execution) -> {
          request.getHeaders().add("User-Agent", "Mozilla/5.0");
          return execution.execute(request, body);
        }));
  }

  @Override
  public String get(final String url) throws RestClientException {
    return restTemplate.getForObject(url, String.class);
  }
}

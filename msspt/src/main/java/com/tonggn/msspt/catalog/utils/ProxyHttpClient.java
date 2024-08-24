package com.tonggn.msspt.catalog.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProxyHttpClient implements HttpClient {

  private static final int TIMEOUT_MILLIS = 5000;
  private final RestTemplate restTemplate;

  public ProxyHttpClient(
      @Value("${mss.proxy-host}") final String proxyHost,
      @Value("${mss.proxy-port}") final int proxyPort
  ) {
    final Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
    final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setProxy(proxy);
    requestFactory.setReadTimeout(TIMEOUT_MILLIS);
    requestFactory.setConnectTimeout(TIMEOUT_MILLIS);
    this.restTemplate = new RestTemplate(requestFactory);
  }

  @Override
  public String get(final String url) throws RestClientException {
    return restTemplate.getForObject(url, String.class);
  }
}

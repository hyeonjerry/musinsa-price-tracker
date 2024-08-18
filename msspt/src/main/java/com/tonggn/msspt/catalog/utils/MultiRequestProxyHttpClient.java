package com.tonggn.msspt.catalog.utils;

import java.net.Proxy;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class MultiRequestProxyHttpClient implements HttpClient {

  private static final int N_THREADS = 10;
  private static final int TIMEOUT_MILLIS = 5000;

  private final List<Proxy> proxies;
  private final ExecutorService executor;
  private int index;

  public MultiRequestProxyHttpClient(final List<Proxy> proxies) {
    this.index = 0;
    this.proxies = proxies;
    this.executor = Executors.newFixedThreadPool(N_THREADS);
  }

  @Override
  public String get(final String url) {
    final List<Proxy> proxies = getNextProxies(N_THREADS);
    final List<Callable<String>> callables = proxies.stream()
        .map(proxy -> (Callable<String>) () -> request(proxy, url))
        .toList();
    try {
      return executor.invokeAny(callables);
    } catch (final InterruptedException | ExecutionException e) {
      log.error(e.getMessage());
      return get(url);
    }
  }

  private String request(final Proxy proxy, final String url) {
    final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setProxy(proxy);
    requestFactory.setReadTimeout(TIMEOUT_MILLIS);
    requestFactory.setConnectTimeout(TIMEOUT_MILLIS);
    final RestTemplate restTemplate = new RestTemplate(requestFactory);
    return restTemplate.getForObject(url, String.class);
  }

  private List<Proxy> getNextProxies(final int n) {
    return IntStream.range(0, n)
        .mapToObj(i -> getNextProxy())
        .toList();
  }

  private Proxy getNextProxy() {
    if (index >= proxies.size()) {
      index = 0;
    }
    return proxies.get(index++);
  }
}

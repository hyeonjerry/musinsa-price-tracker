package com.tonggn.msspt.catalog.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class RotatingProxyMultiRequestHttpClient implements HttpClient {

  private static final int N_THREADS = 20;
  private static final int TIMEOUT = 15_000;

  private final ExecutorService executor;
  private final String proxyListUrl;

  private List<Proxy> proxies = List.of();
  private int index = 0;

  public RotatingProxyMultiRequestHttpClient(
      @Value("${mss.proxy-list-url}") final String proxyListUrl
  ) {
    this.executor = Executors.newFixedThreadPool(N_THREADS);
    this.proxyListUrl = proxyListUrl;
  }

  @Override
  public String get(final String url) {
    final List<Proxy> proxies = extractSampleProxies();
    final List<Callable<String>> jobs = proxies.stream()
        .map(proxy -> (Callable<String>) () -> request(url, proxy))
        .toList();
    try {
      return executor.invokeAny(jobs, TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (final InterruptedException | ExecutionException | TimeoutException e) {
      switch (e.getClass().getSimpleName()) {
        case "InterruptedException":
          Thread.currentThread().interrupt();
          log.error("Thread Interrupted by {}", e.getMessage());
          break;
        case "ExecutionException":
          log.error("Execution exception occurred {}", e.getMessage());
          break;
        case "TimeoutException":
          log.error("Timeout exception occurred {}", e.getMessage());
          break;
      }
      return get(url);
    }
  }

  private List<Proxy> extractSampleProxies() {
    if (index + N_THREADS >= proxies.size()) {
      updateProxies(proxyListUrl);
      index = 0;
    }
    final int offset = N_THREADS / 2;
    index += offset;
    return proxies.subList(index - offset, index + offset);
  }

  private String request(final String url, final Proxy proxy) {
    final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setProxy(proxy);
    requestFactory.setReadTimeout(TIMEOUT);
    requestFactory.setConnectTimeout(TIMEOUT);
    final RestTemplate restTemplate = new RestTemplate(requestFactory);
    return restTemplate.getForObject(url, String.class);
  }

  private void updateProxies(final String proxyListUrl) {
    final RestTemplate restTemplate = new RestTemplate();
    final String[] addresses = restTemplate.getForObject(proxyListUrl, String.class).split("\n");
    this.proxies = Arrays.stream(addresses)
        .map(this::mapToProxy)
        .toList();
  }

  private Proxy mapToProxy(final String address) {
    final String[] parts = address.split(":");
    final String host = parts[0];
    final int port = Integer.parseInt(parts[1]);
    return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
  }
}

package com.tonggn.msspt.catalog.utils;

import java.net.Proxy;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RotatingProxyMultiRequestHttpClient implements HttpClient {

  private static final int N_THREADS = 30;
  private static final int TIMEOUT = 15_000;

  private final ProxyManager proxyManager;
  private final ExecutorService executor;
  private final String proxyListUrl;

  public RotatingProxyMultiRequestHttpClient(final String proxyListUrl) {
    this.executor = Executors.newFixedThreadPool(N_THREADS);
    this.proxyManager = new ProxyManager(N_THREADS, 5);
    this.proxyListUrl = proxyListUrl;
  }

  @Override
  public String get(final String url) {
    final List<Proxy> proxies = proxyManager.sampleRecurrentProxies();
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
      proxyManager.dropSampledProxies();
      return get(url);
    }
  }

  private String request(final String url, final Proxy proxy) {
    final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setProxy(proxy);
    requestFactory.setReadTimeout(TIMEOUT);
    requestFactory.setConnectTimeout(TIMEOUT);
    final RestTemplate restTemplate = new RestTemplate(requestFactory);
    return restTemplate.getForObject(url, String.class);
  }

  private class ProxyManager {

    private final int sampleSize;
    private final int offset;

    private List<Proxy> proxies;
    private int index;

    public ProxyManager(final int sampleSize, final int offset) {
      this.sampleSize = sampleSize;
      this.offset = offset;
      this.proxies = List.of();
      this.index = 0;
    }

    public List<Proxy> sampleRecurrentProxies() {
      if (index + sampleSize >= proxies.size()) {
        this.proxies = HttpProxyFetcher.fetchProxies(proxyListUrl);
        index = 0;
      }
      final int start = index;
      final int end = index + sampleSize;
      index += offset;
      return proxies.subList(start, end);
    }

    public void dropSampledProxies() {
      index -= offset;
      index += sampleSize;
    }
  }
}

package com.suva.performancetestapp.configuration;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {
  @Value("${app.baseUrl}")
  private String baseUrl;

  @Bean
  @Primary
  public RestClient restClient() {
    PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
    poolingConnManager.setMaxTotal(50);
    poolingConnManager.setDefaultMaxPerRoute(50);

    HttpClient httpClient = HttpClients.custom()
        .setConnectionManager(poolingConnManager)
        .build();

    return RestClient.builder()
        .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
        .baseUrl(baseUrl)
        .build();
  }
}

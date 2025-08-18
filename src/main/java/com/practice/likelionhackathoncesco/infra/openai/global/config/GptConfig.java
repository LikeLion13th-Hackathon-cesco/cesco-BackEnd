package com.practice.likelionhackathoncesco.infra.openai.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class GptConfig {

  @Value("${openai.api.key}")
  private String secretKey;

  @Value("${openai.api.model}")
  private String model;

  @Value("${openai.api.url}")
  private String url;

  // 아래 두 메소드는 없어도 되긴함
  @Bean
  public RestTemplate gptRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  public HttpHeaders httpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(secretKey);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}

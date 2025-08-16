package com.practice.likelionhackathoncesco.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class NaverOcrConfig {

  @Value("${naver.ocr.invoke-url}")
  private String invokeUrl;

  @Value("${naver.ocr.secret-key}")
  private String secretKey;

  // Spring Boot 2.4 이상에서는 RestTemplate이 자동으로 등록되지 않기 때문에 직접 Bean으로 등록해줘야 합니다.
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}

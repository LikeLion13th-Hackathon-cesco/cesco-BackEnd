package com.practice.likelionhackathoncesco.global.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Value("${cors.allowed-origins}")
  private String allowedOrigins;

  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 쉼표로 분할해서 배열로 변환
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    // OPTIONS 메소드 추가 (CORS preflight 요청을 위해 필수)
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    // 모든 헤더 허용 (Swagger에서 필요할 수 있는 헤더들 때문에)
    configuration.setAllowedHeaders(Arrays.asList("*"));
    // 쿠키나 인증 정보를 포함하는 요청 허용
    configuration.setAllowCredentials(true);
    // 모든 경로에 대해 위의 CORS 설정을 적용
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

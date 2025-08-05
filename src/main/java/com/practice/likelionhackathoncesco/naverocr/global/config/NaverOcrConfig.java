package com.practice.likelionhackathoncesco.naverocr.global;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class NaverOcrConfig {

  @Value("${naver.ocr.invoke-url}")
  private String invokeUrl;
  @Value("${naver.ocr.secret-key}")
  private String secretKey;

}

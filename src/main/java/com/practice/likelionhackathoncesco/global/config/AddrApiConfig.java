package com.practice.likelionhackathoncesco.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class AddrApiConfig {

  @Value("${ADDR_KEY}")
  private String confmKey;

  @Value("${ADDR_URL}")
  private String confmUrl;
}

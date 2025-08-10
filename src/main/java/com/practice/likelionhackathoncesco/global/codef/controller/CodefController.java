package com.practice.likelionhackathoncesco.global.codef.controller;

import com.practice.likelionhackathoncesco.global.codef.service.AccessTokenService;
import com.practice.likelionhackathoncesco.global.codef.service.CodefClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CodefController {

  private final AccessTokenService accessTokenService;
  private final CodefClient codefClient;

  @GetMapping("/token")
  public String getToken() {
    String token = accessTokenService.getValidToken();
    log.info("토큰 발급 성공");
    return token;
  }
}

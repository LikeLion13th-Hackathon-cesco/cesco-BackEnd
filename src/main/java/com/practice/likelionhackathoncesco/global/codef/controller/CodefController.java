package com.practice.likelionhackathoncesco.global.codef.controller;

import com.practice.likelionhackathoncesco.global.codef.service.CodefTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CodefController {

  private final CodefTokenService codefTokenService;

  @GetMapping("/token")
  public String getToken() throws Exception {
    return codefTokenService.getAccessToken();
  }
}

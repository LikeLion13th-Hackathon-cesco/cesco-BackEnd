package com.practice.likelionhackathoncesco.global.codef.controller;

import com.practice.likelionhackathoncesco.global.codef.dto.response.CodefOfficeResponse;
import com.practice.likelionhackathoncesco.global.codef.service.AccessTokenService;
import com.practice.likelionhackathoncesco.global.codef.service.CallCodefApi;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CodefController {

  private final AccessTokenService accessTokenService;
  private final CallCodefApi callCodefApi;

  @GetMapping("/token")
  public String getToken() {
    String token = accessTokenService.getValidToken();
    log.info("토큰 발급 성공");
    return token;
  }

  @Operation(summary = "공동주택 공기가격 반환 API", description = "도로명 주소에 따른 공동주택 공기가격을 반환하는 API")
  @PostMapping("/house-price/{reportId}")
  public ResponseEntity<CodefOfficeResponse> textCodef(@PathVariable Long reportId) {
    CodefOfficeResponse response = callCodefApi.extractPrice(reportId);
    return ResponseEntity.ok(response);
  }
}

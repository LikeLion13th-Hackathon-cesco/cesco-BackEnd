package com.practice.likelionhackathoncesco.global.codef.service;


import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// codef api 사용을 위해 엑세스 토큰 발급 요청
@Service
@RequiredArgsConstructor
@Slf4j
public class CodefClient {
  @Value("${codef.client-id}")
  private String clientId;

  @Value("${codef.client-secret}")
  private String clientSecret;

  @Value("${codef.public-key}")
  private String publicKey;

  public String requestNewToken() { // 토큰 생성
    try {
      EasyCodef codef = new EasyCodef();
      codef.setClientInfo(clientId, clientSecret);
      codef.setPublicKey(publicKey);

      log.info("CODEF API 토큰 발급 요청 시작");
      String token = codef.requestNewToken(EasyCodefServiceType.API); // 이거 뭐로 해야할까..공식문서에서 본거 같은데 어딧거 봤는지 모르겠음
      log.info("CODEF API 토큰 발급 성공");

      return token;
    } catch (Exception e) {
      log.error("CODEF API 토큰 발급 실패: {}", e.getMessage(), e);
      throw new RuntimeException("CODEF 토큰 발급 API 호출 실패", e);
    }
  }


}

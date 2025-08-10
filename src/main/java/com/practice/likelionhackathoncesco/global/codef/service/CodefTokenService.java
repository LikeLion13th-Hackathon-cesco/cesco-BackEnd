package com.practice.likelionhackathoncesco.global.codef.service;


import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// codef api 사용을 위해 엑세스 토큰 발급 요청
@Service
public class CodefTokenService {
  @Value("${codef.client-id}")
  private String clientId;

  @Value("${codef.client-secret}")
  private String clientSecret;

  @Value("${codef.public-key}")
  private String publicKey;

  public String getAccessToken() throws Exception {
    EasyCodef codef = new EasyCodef();

    // 정식 서비스 정보 설정
    codef.setClientInfo(clientId, clientSecret);

    // RSA 암호화를 위한 공개키 설정
    codef.setPublicKey(publicKey);

    // 액세스 토큰 발급 (SANDBOX)
    return codef.requestToken(EasyCodefServiceType.SANDBOX);
  }


}

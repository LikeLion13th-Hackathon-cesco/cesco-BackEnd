package com.practice.likelionhackathoncesco.global.codef.service;

import com.practice.likelionhackathoncesco.global.codef.entity.AccessToken;
import com.practice.likelionhackathoncesco.global.codef.repository.AccessTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessTokenService {

  private final AccessTokenRepository accessTokenRepository;
  private final CodefClient codefClient;

  @Transactional
  public String getValidToken() { // 토큰 유효성 체크하고 DB 저장
    try {
      AccessToken accessToken = accessTokenRepository.findById("codef").orElse(null);

      if (accessToken == null || isExpired(accessToken)) {
        log.info("엑세스 토큰이 없거나 만료되어 새로운 엑세스 토큰을 발급합니다.");

        String newToken = codefClient.requestNewToken();

        // null 체크 추가
        if (newToken == null || newToken.trim().isEmpty()) {
          throw new RuntimeException("토큰 발급 실패: null 또는 빈 값 반환");
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        if (accessToken == null) {
          accessToken = new AccessToken();
          accessToken.setId("codef");
        }

        accessToken.setAccessToken(newToken);
        accessToken.setExpiresAt(expiresAt);
        accessTokenRepository.save(accessToken);

        log.info("토큰 저장 완료. 토큰 길이: {}", newToken.length());
      }

      return accessToken.getAccessToken();
    } catch (Exception e) {
      log.error("토큰 처리 중 오류: {}", e.getMessage(), e);
      throw new RuntimeException("토큰 처리 실패", e);
    }
  }

  private boolean isExpired(AccessToken token) {
    return token.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(5)); // 여유 5분 전 만료 판단
  }

}

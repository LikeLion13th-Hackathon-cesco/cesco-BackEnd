package com.practice.likelionhackathoncesco.domain.user;

import static com.practice.likelionhackathoncesco.domain.user.entity.PayStatus.UNPAID;

import com.practice.likelionhackathoncesco.domain.user.entity.PayStatus;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

  private final UserRepository userRepository;

  @Value("${app.default.username:cesco}")
  private String defaultUsername;

  @PostConstruct
  public void initDefaultData() { // Spring 실행 시 가장 먼저 실행되면서 고정 사용자를 생성한다
    if (!userRepository.existsByUsername(defaultUsername)) {
      User defaultUser = User.builder()
          .username(defaultUsername) // 고정 사용자 이름
          .credit(0) // 크레딧 초기 값 0
          .payStatus(UNPAID) // 초기 미결제 상태
          .build();

      userRepository.save(defaultUser);
      log.info("해커톤용 기본 사용자 생성: {}", defaultUsername);
    }
  }


}

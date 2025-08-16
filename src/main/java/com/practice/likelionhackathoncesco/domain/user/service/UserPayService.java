package com.practice.likelionhackathoncesco.domain.user.service;

import com.practice.likelionhackathoncesco.domain.user.dto.response.PayResponse;
import com.practice.likelionhackathoncesco.domain.user.entity.PayStatus;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.exception.UserErrorCode;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPayService {

  private final UserRepository userRepository;

  @Transactional
  public PayResponse completePayment(Long userId) { // 결제 진행 메서드
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if(user.getPayStatus() == PayStatus.PAID) {
      log.info("사용자 {}는 이미 결제를 완료했습니다. 만료일: {}", user.getUsername(), user.getExpirationDate());
      throw new CustomException(UserErrorCode.USER_ALREADY_PAID);
    }

    user.processPayment(); // 사용자 결제 처리
    userRepository.save(user); // DB 저장

    log.info("사용자 {}의 결제가 완료되었습니다. 만료일: {}", user.getUsername(), user.getExpirationDate());

    return PayResponse.builder()
        .payStatus(PayStatus.PAID)
        .build();
  }

  // 결제 만료일이 지난 사용자를 조회하고 결제 상태를 다시 미결제 상태로 바꿔주고 DB저장
  @Transactional
  @Scheduled(cron = "0 1 0 * * *") // 매일 00:01분에 실행
  public void expirePaymentCheck() {
    log.info("=== 결제 만료 체크 시작 ===");
    List<User> expiredUsers = userRepository.findByPayStatusAndExpirationDateBefore(
        PayStatus.PAID, LocalDateTime.now());

    for (User user : expiredUsers) {
      user.updateExpire(); // 미결제 상태로 변경과 결제 만료일 필드 null값으로 설정
      log.info("사용자 {}의 결제가 만료되었습니다.", user.getUsername());
    }

    if (!expiredUsers.isEmpty()) {
      userRepository.saveAll(expiredUsers);
    }

    log.info("=== 결제 만료 체크 완료: {}건 처리 ===", expiredUsers.size());
  }

  // 결제 만료 여부 확인 (유효 : true / 만료 : false)
  @Transactional(readOnly = true)
  public boolean isUserPaid(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    return !user.isPaymentExpired(); // 결제 만료 사용자 false 반환
  }

}

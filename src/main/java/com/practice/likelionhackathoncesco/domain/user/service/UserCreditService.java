package com.practice.likelionhackathoncesco.domain.user.service;

import com.practice.likelionhackathoncesco.domain.user.dto.response.CreditResponse;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.exception.UserErrorCode;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreditService {

  private final UserRepository userRepository;

  @Transactional
  public CreditResponse PostCreditStep(Long userId) {

    log.info("[UserCreditService] 사용자별 크레딧 지급 기준 충족 여부 조회 시도");
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    Integer postCount = user.getPostCount();

    log.info("[UserCreditService] 사용자별 생성한 게시글 개수 조회 : postCount={}", postCount);

    if (postCount < 1) {
      return toCreditResponse(false, false, false);
    } else if (postCount < 5) {
      return toCreditResponse(true, false, false);
    } else if (postCount < 15) {
      return toCreditResponse(true, true, false);
    } else {
      return toCreditResponse(true, true, true);
    }
  }

  public CreditResponse toCreditResponse(Boolean one, Boolean five, Boolean fifteen) {
    return CreditResponse.builder()
        .postCountOne(one)
        .postCountFive(five)
        .postCountFifteen(fifteen)
        .build();
  }
}

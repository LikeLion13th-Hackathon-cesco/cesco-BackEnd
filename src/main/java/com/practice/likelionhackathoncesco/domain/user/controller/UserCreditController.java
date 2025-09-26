package com.practice.likelionhackathoncesco.domain.user.controller;

import com.practice.likelionhackathoncesco.domain.user.dto.response.CreditResponse;
import com.practice.likelionhackathoncesco.domain.user.dto.response.UserCreditResponse;
import com.practice.likelionhackathoncesco.domain.user.service.UserCreditService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "credit", description = "사용자 크레딧 관련 API")
public class UserCreditController {

  private final UserCreditService userCreditService;

  @Operation(summary = "크레딧 지급 기준 충족 여부 조회", description = "마이페이지에서 사용자가 충족한 크레딧 지급 기준 조회하는 API")
  @GetMapping("/credit/{userId}")
  public ResponseEntity<BaseResponse<CreditResponse>> getCreditStep(
      @Parameter(description = "사용자 ID") @PathVariable Long userId) {
    CreditResponse creditResponse = userCreditService.PostCreditStep(userId);
    return ResponseEntity.ok(BaseResponse.success("크레딧 지급 기준 충족 여부 조회 완료", creditResponse));
  }

  @Operation(summary = "사용자 보유 크레딧 조회", description = "사용자의 보유 크레딧을 조회하는 API")
  @GetMapping("/hascredit/{userId}")
  public ResponseEntity<BaseResponse<UserCreditResponse>> getCredit(
      @Parameter(description = "사용자 ID") @PathVariable Long userId) {
    UserCreditResponse userCreditResponse = userCreditService.getUserCredit(userId);
    return ResponseEntity.ok(BaseResponse.success("사용자 보유 크레딧 조회 완료", userCreditResponse));
  }

}

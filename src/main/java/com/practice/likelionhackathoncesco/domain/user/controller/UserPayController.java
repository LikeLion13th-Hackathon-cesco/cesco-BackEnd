package com.practice.likelionhackathoncesco.domain.user.controller;

import com.practice.likelionhackathoncesco.domain.user.dto.response.PayResponse;
import com.practice.likelionhackathoncesco.domain.user.service.UserPayService;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payemnt")
@Tag(name = "Payment", description = "Plus 요금제 관련 API")
public class UserPayController {

  private final UserPayService userPayService;

  @Operation(summary = "Plus 요금제 결제 API", description = "Plus 요금제의 결제 버튼을 누르면 결제 처리 되는 API")
  @PostMapping("/{userId}")
  public ResponseEntity<BaseResponse<PayResponse>> processPayment(@PathVariable Long userId) {
    PayResponse result = userPayService.completePayment(userId);
    return ResponseEntity.ok(BaseResponse.success("결제 완료", result));
  }

}

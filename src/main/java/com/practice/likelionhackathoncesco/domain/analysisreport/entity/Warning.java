package com.practice.likelionhackathoncesco.domain.analysisreport.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Warning {
  WARN("해당 매물의 임대인은 저희 서비스에서 신고당한 이력이 있는 임대인이니 각별한 주의가 필요합니다."), // 신고당한 이력이 있는 임대인일때
  DEFAULT(""); // 신고당한 임대인 테이블에 없는 임대인 이거나 결제하지 않은 사용자일때

  private final String message;
}

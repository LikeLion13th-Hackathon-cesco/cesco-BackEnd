package com.practice.likelionhackathoncesco.domain.analysisreport.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Comment {
  DANGER("해당 부동산은 거래 시 위험 부담이 있습니다"), // 0-3점
  CAUTION("해당 부동산은 거래에 주의가 필요합니다"), // 3-7점
  SAFE("해당 부동산은 비교적 안전합니다"); // 7-10점

  private final String message;
}

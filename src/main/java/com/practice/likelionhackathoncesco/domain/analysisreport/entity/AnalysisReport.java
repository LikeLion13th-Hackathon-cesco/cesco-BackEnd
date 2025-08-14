package com.practice.likelionhackathoncesco.domain.analysisreport.entity;

import com.practice.likelionhackathoncesco.domain.commonfile.BaseFileEntity;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부 객체 생성 방지하기 위한 접근제어자 설정
@Table(name = "analysis_reports")
public class AnalysisReport extends BaseFileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reportId; // 분석 레포트 고유 번호

  // 분석 결과 관련
/*  @Lob
  @Column(name = "ocr_text")
  private String ocrText; // 추출된 텍스트 저장*/

  @Column(name = "safety_score")
  private Double safetyScore; // 안전 점수

  @Column(name = "insurance_percent")
  private Integer insurancePercent; // 보험 가입 여부 가능성

  @Lob // gpt 응답이 들어가기 때문에 긴 문자열로 저장
  @Column(name = "description")
  private String description; // 안전 점수 설명

  // 처리 상태 관리
  @Enumerated(EnumType.STRING)
  @Column(name = "processing_status", nullable = false)
  private ProcessingStatus processingStatus;

  // 고정된 사용자 매핑
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  // 추출된 텍스트 DB에 저장
/*  public void updateOcrText(String ocrText) {
    this.ocrText = ocrText;
  }*/

  // 진행 상태 DB 업데이트
  public void updateProcessingStatus(ProcessingStatus processingStatus) {
    this.processingStatus = processingStatus;
  }
}

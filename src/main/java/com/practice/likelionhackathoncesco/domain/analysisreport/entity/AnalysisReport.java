package com.practice.likelionhackathoncesco.domain.analysisreport.entity;

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

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부 객체 생성 방지하기 위한 접근제어자 설정
@AllArgsConstructor
@Table(name = "analysis_reports")
public class AnalysisReport extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reportId; // 분석 레포트 고유 번호

  // 등기부등본 관련
  @Column(name = "file_name", nullable = false)
  private String fileName; // 업로드한 파일 이름

  @Column(name = "s3_key", nullable = false)
  private String s3Key; // s3 객체 식별 키
  // s3 경로 -> 추후에 s3key로 객체 url(웹 형태) 생성 (동적 생성 가능)

  // 분석 결과 관련
  @Column(name = "safety_score")
  private Double safetyScore; // 안전 점수

  @Column(name = "insurance_percent")
  private Integer insurancePercent; // 보험 가입 여부 가능성

  @Lob // gpt 응답이 들어가기 때문에 긴 문자열로 저장
  @Column(name = "description")
  private String description; // 안전 점수 설명

  // OCR 관련
  @Lob // 긴 텍스트를 위해 사용
  @Column(name = "ocr_text")
  private String ocrText; // 추출한 등기부등본의 원본 내용 저장(원본 데이터 저장 + 검증 목적)

  // 감지된 위험 키워드
  @Column(name = "detected_keywords")
  private String detectedKeywords; // 감지된 모든 문자열을 한 문자열로 저장

  // 처리 상태 관리
  @Enumerated(EnumType.STRING)
  @Column(name = "processing_status", nullable = false)
  private ProcessingStatus processingStatus;

  // 고정된 사용자 매핑
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
}

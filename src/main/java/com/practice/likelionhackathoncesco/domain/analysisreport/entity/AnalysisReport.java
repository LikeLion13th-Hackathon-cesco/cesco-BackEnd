package com.practice.likelionhackathoncesco.domain.analysisreport.entity;

import com.practice.likelionhackathoncesco.domain.commonfile.BaseFileEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlType.DEFAULT;
import lombok.AccessLevel;
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

  @Column(name = "address")
  private String address; // 해당 등기부등본 부동산의 주소

  @Column(name = "official_price")
  private Double officialPrice; // 공시가격

  // 분석 결과 관련
  @Column(name = "safety_score")
  private Double safetyScore; // 안전 점수

  @Column(name = "short_description", columnDefinition = "TEXT")
  private String summary; // 한줄 요약

  @Enumerated(EnumType.STRING)
  @Column(name = "comment")
  private Comment comment; // 점수에 따른 코멘트(이번에 추가한거)

  @Lob // gpt 응답이 들어가기 때문에 긴 문자열로 저장
  @Column(name = "safety_description", columnDefinition = "TEXT")
  private String safetyDescription; // 안전 점수 설명

  @Lob // gpt 응답이 들어가기 때문에 긴 문자열로 저장
  @Column(name = "insurance_description", columnDefinition = "TEXT")
  private String insuranceDescription; // 보증보험가입 가능 여부 설명

  // 처리 상태 관리
  @Enumerated(EnumType.STRING)
  @Column(name = "processing_status", nullable = false)
  private ProcessingStatus processingStatus;

  // plus 요금제 결제한 사용자에게만 신고당한 이력이 있는 임대인이라는것을 알려줄 문구
  @Enumerated(EnumType.STRING)
  @Column(name="warning")
  private Warning warning = Warning.DEFAULT;  // 일반적인 경우에는 아무것도 안뜨는게 맞는거임

  // 진행 상태 DB 업데이트
  public void updateProcessingStatus(ProcessingStatus processingStatus) {
    this.processingStatus = processingStatus;
  }

  public void updateComment(Comment comment) {
    this.comment = comment;
  }

  // 분석 후 DB 업데이트
  public void update(
      String address,
      Double safetyScore,
      String summary,
      String safetyDescription,
      String insuranceDescription,
      Warning warning) {
    this.address = address;
    this.safetyScore = safetyScore;
    this.summary = summary;
    this.safetyDescription = safetyDescription;
    this.insuranceDescription = insuranceDescription;
    this.warning = warning;
  }
}

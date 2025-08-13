package com.practice.likelionhackathoncesco.domain.fraudreport.entity;

import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "complaint_reports")
public class ComplaintReport extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long complaintId; // 고소장 고유 번호

  @Column(name = "file_name", nullable = false)
  private String fileName; // 업로드한 파일 이름

  @Column(name = "s3_key", nullable = false)
  private String s3Key; // s3 객체 식별 키

  @Enumerated(EnumType.STRING)
  @Column(name = "report_status")
  private ReportStatus reportStatus; // 신고 상태

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fraud_register_report_id", unique = true) // unique 제약조건으로 1:1 보장
  private FraudRegisterReport fraudRegisterReport;

  // 고정된 사용자 매핑
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;
}

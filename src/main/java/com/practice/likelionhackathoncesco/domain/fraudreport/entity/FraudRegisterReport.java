package com.practice.likelionhackathoncesco.domain.fraudreport.entity;

import com.practice.likelionhackathoncesco.domain.commonfile.BaseFileEntity;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
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
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부 객체 생성 방지하기 위한 접근제어자 설정
@Table(name = "fraud_register_reports")
public class FraudRegisterReport extends BaseFileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long fraudRegisterReportId; // 사기 등기부등본 고유 번호

  @Enumerated(EnumType.STRING)
  @Column(name = "report_status", nullable = false)
  private ReportStatus reportStatus; // 신고 상태

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "faker_id", unique = true)
  private Faker faker;

  @OneToOne(mappedBy = "fraudRegisterReport", cascade = CascadeType.ALL, orphanRemoval = true)
  private ComplaintReport complaintReport;

  // 고정된 사용자 매핑
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;


}

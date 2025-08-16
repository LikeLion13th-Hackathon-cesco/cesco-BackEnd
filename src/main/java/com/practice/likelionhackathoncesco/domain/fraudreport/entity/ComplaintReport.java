package com.practice.likelionhackathoncesco.domain.fraudreport.entity;

import com.practice.likelionhackathoncesco.domain.commonfile.BaseFileEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부 객체 생성 방지하기 위한 접근제어자 설정
@Table(name = "complaint_reports")
public class ComplaintReport extends BaseFileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long complaintReportId; // 고소장 고유 번호

  @Enumerated(EnumType.STRING)
  @Column(name = "report_status")
  private ReportStatus reportStatus; // 신고 상태
}

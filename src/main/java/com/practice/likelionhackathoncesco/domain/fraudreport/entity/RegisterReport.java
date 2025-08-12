package com.practice.likelionhackathoncesco.domain.fraudreport.entity;

import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "fraud_register_reports")
public class RegisterReport extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long fraudRegisterReportId; // 고소장 고유 번호

  @Column(name = "file_name", nullable = false)
  private String fileName; // 업로드한 파일 이름

  @Column(name = "s3_key", nullable = false)
  private String s3Key; // s3 객체 식별 키

}

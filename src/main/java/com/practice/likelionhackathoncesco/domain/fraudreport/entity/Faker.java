package com.practice.likelionhackathoncesco.domain.fraudreport.entity;

import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "fakers")
public class Faker extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long fakerId; // 사기꾼 id

  @Column(name = "faker_name", nullable = false)
  private String fakerName; // 사기꾼 이름

  @Column(name = "resident_num", nullable = false)
  private String residentNum; // 사기꾼 주민번호 앞자리

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fraudRegisterReportId", nullable = false) // user_id로 통일 (userId → user_id)
  private FraudRegisterReport fraudRegisterReport;

}

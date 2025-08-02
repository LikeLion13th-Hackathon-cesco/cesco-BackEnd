package com.practice.likelionhackathoncesco.domain.user.entity;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId; // 사용자 고유 번호 (어차피 1명)

  @Column(name = "username")
  private String username; // 사용자 이름 -> cesco 고정(스프링 실행 시 생성)

  @Column(name = "credit")
  private Integer credit; // 사용자 크레딧

  @Enumerated(EnumType.STRING)
  @Column(name = "pay_status", nullable = false)
  private PayStatus payStatus; // 결제 유무

  // analysisReport 테이블의 user 필드와 연관 -> user는 analysisReport를 여러개 가짐
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AnalysisReport> reports = new ArrayList<>();

}

package com.practice.likelionhackathoncesco.domain.user.entity;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ComplaintReport;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.FraudRegisterReport;
import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
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
import java.time.LocalDateTime;
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
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId; // 사용자 고유 번호 (어차피 1명)

  @Column(name = "username")
  private String username; // 사용자 이름 -> cesco 고정(스프링 실행 시 생성)

  @Column(name = "credit")
  private Integer credit; // 사용자 크레딧

  @Column(name = "expiration_date")
  private LocalDateTime expirationDate; // 결제상태가 PAID일때 결제 만료일

  @Enumerated(EnumType.STRING)
  @Column(name = "pay_status", nullable = false)
  private PayStatus payStatus; // 결제 유무

  // 사용자가 생성한 게시글 개수 (게시글을 삭제하더라도 줄어들지 않음)
  @Column(name = "post_count", nullable = false)
  @Builder.Default
  private Integer postCount = 0; // 기본값 0으로 설정하려고

  // analysisReport 테이블의 user 필드와 연관 -> user는 analysisReport를 여러개 가짐
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<AnalysisReport> reports = new ArrayList<>();

  // FraudReport 테이블의 user 필드와 연관 -> user는 FraudReport를 여러개 가짐
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FraudRegisterReport> fraudRegisterReports = new ArrayList<>();

  // ComplaintReport 테이블의 user 필드와 연관 -> user는 ComplaintReport를 여러개 가짐
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ComplaintReport> complaintReport = new ArrayList<>();

  // 결제 상태를 PAID로 바꾸고 결제 만료일을 한달 뒤로 저장
  public void processPayment() {
    this.payStatus = PayStatus.PAID;
    this.expirationDate = LocalDateTime.now().plusMonths(1);
  }

  // 결제 만료가 되면 expirationDate 필드는 null로 변경
  public void updateExpire() {
    this.payStatus = PayStatus.UNPAID;
    this.expirationDate = null;
  }

  // 결제 만료 여부 확인 메서드 (만료 된 사용자 true 반환)
  public boolean isPaymentExpired() {
    if (payStatus == PayStatus.UNPAID || expirationDate == null) {
      return true; // 미결제 상태는 true 반환
    }
    return LocalDateTime.now().isAfter(expirationDate); // 현재 시간을 기준으로 결제 만료일 이후 인지
  }

  // 크레딧 추가하는 메서드
  public void addCredits(int credit) {
    this.credit += credit;
  }

  // 역대 생성한 게시글 수 증가시키는 메소드
  public void addPostCount() {
    this.postCount += 1;
  }
}

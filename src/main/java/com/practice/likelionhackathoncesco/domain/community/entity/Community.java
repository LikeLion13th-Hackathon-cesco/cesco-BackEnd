package com.practice.likelionhackathoncesco.domain.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="community")
public class Community {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long communityId;   // 커뮤니티 id

  @Column(nullable = false)
  private String roadCode;   // 도로명코드(12) = 시군구코드(5) + 도로명번호(7) -> 문자

  @Column(nullable = false)
  private String buildingNumber;    // 건물본번(5) -> 숫자(데이터형변환 필요)

}

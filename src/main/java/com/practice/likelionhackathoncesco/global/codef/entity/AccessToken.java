package com.practice.likelionhackathoncesco.global.codef.entity;

import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "codef_token")
public class AccessToken extends BaseTimeEntity {

  @Id
  private String id = "codef"; // 단일 토큰이라 id는 고정 값으로 설정

  @Lob
  private String accessToken; // 엑세스 토큰

  private LocalDateTime expiresAt; // 엑세스 토큰 만료 날짜 (7일)

}

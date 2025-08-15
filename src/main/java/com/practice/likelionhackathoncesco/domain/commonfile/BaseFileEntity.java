package com.practice.likelionhackathoncesco.domain.commonfile;

import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@MappedSuperclass
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseFileEntity extends BaseTimeEntity {

  @Column(name = "file_name", nullable = false)
  private String fileName; // 업로드한 파일 이름

  @Column(name = "s3_key", nullable = false)
  private String s3Key; // s3 객체 식별 키

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false) // user_id로 통일 (userId → user_id)
  private User user;

  protected BaseFileEntity() {
    super(); // 부모 기본 생성자 호출
  }

}

package com.practice.likelionhackathoncesco.domain.post.entity;

import com.practice.likelionhackathoncesco.domain.community.entity.Community;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="post")
public class Post extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long postId;  // 게시글 고유 번호

  @Column(nullable = false)
  private String content;   // 게시글 내용

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id",nullable = false)
  private User user;  // 사용자 ID(FK)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "community_id",nullable = false)
  private Community community;  // 커뮤니티 ID(FK)

  public void update(String content) {
    this.content = content;
  }

}

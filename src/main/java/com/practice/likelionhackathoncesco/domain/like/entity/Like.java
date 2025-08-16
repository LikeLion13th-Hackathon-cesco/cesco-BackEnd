package com.practice.likelionhackathoncesco.domain.like.entity;

import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "likes",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"user_id", "post_id"}) // 한명의 사용자가 하나의 게시글에 대해 여러 좋아요 생성 방지
    })
public class Like { // 왠만하면 복합키를 만드는것보다 엔티티 하나, 단일키로 하고 + 좋아요 존재여부 DB차원 판단 -> 이렇게 해야 동시성 고려한 코드

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long likeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;
}

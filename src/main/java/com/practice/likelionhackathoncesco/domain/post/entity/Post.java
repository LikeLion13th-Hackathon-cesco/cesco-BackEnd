package com.practice.likelionhackathoncesco.domain.post.entity;

import com.practice.likelionhackathoncesco.domain.comment.entity.Comment;
import com.practice.likelionhackathoncesco.domain.like.entity.Like;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class Post extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long postId; // 게시글 고유 번호

  @Column(nullable = false)
  private String content; // 게시글 내용

  @Column(nullable = false)
  private String roadCode; // 도로명 코드

  @Column(nullable = false)
  private String buildingNumber; // 건물 본번

  @Column(nullable = false)
  private int likeCount; // 좋아요 수

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // 사용자 ID(FK)

  @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  public void update(String content) {
    this.content = content;
  }

  public void increaseLikeCount() {
    this.likeCount++;
  }

  public void decreaseLikeCount() {
    if (this.likeCount > 0) {
      this.likeCount--;
    }
  }
}

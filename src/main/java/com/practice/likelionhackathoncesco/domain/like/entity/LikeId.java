package com.practice.likelionhackathoncesco.domain.like.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable // 이거 외래키 조합으로 기본키 만들때 필요
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LikeId {

  private Long postId;  // 게시글 고유 번호
  private Long communityId;  // 커뮤니티 고유 번호
}

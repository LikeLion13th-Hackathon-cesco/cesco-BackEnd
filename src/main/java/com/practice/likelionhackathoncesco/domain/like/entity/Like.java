package com.practice.likelionhackathoncesco.domain.like.entity;

import com.practice.likelionhackathoncesco.domain.community.entity.Community;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name="likes")
public class Like {

  @EmbeddedId
  private LikeId id;

  @ManyToOne
  @MapsId("postId")
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne
  @MapsId("communityId")
  @JoinColumn(name = "community_id", nullable = false)
  private Community community;
}

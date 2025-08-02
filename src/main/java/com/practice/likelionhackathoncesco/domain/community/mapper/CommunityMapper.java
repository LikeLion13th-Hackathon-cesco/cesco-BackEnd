package com.practice.likelionhackathoncesco.domain.community.mapper;

import com.practice.likelionhackathoncesco.domain.community.dto.response.CommunityResponse;
import com.practice.likelionhackathoncesco.domain.community.entity.Community;
import com.practice.likelionhackathoncesco.domain.community.repository.CommunityRepository;
import org.springframework.stereotype.Component;

@Component
public class CommunityMapper {

  public CommunityResponse toCommunityResponse(Community community) {
    return CommunityResponse.builder()
        .communityId(community.getCommunityId())
        .roadCode(community.getRoadCode())
        .buildingNumber(community.getBuildingNumber())
        .build();
  }
}

package com.practice.likelionhackathoncesco.domain.community.service;

import com.practice.likelionhackathoncesco.domain.community.dto.response.CommunityResponse;
import com.practice.likelionhackathoncesco.domain.community.entity.Community;
import com.practice.likelionhackathoncesco.domain.community.exception.CommunityErrorCode;
import com.practice.likelionhackathoncesco.domain.community.mapper.CommunityMapper;
import com.practice.likelionhackathoncesco.domain.community.repository.CommunityRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {
  private final CommunityRepository communityRepository;
  private final CommunityMapper communityMapper;

  // (도로명코드 + 건물본번) 이 검색한 건물의 도로명주소와 건물본번과 같을때만 조회


  /*
  // (도로명코드 + 건물본번)별 커뮤니티 조회
  @Transactional
  public CommunityResponse getCommunityByRoadCodeAndBuildingNumber(String roadCode, String buildingNumber) {
    Community community = communityRepository.findByRoadCodeAndBuildingNumber(roadCode, buildingNumber).orElseThrow(()->new CustomException(
        CommunityErrorCode.COMMUNITY_NOT_FOUND));
    return communityMapper.toCommunityResponse(community);
  }
  */

}

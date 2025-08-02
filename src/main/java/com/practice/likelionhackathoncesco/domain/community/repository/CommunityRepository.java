package com.practice.likelionhackathoncesco.domain.community.repository;

import com.practice.likelionhackathoncesco.domain.community.entity.Community;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {

  // 도로명코드와 건물본번에 의한 커뮤니티 조회 -> for 검색 결과의 (도로명코드+건물본번)이 같은 커뮤니티 조회를 위해
  Optional<Community> findByRoadCodeAndBuildingNumber(String rodeCode, String buildingNumber);

}

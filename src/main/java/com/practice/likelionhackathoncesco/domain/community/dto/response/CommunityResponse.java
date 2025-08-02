package com.practice.likelionhackathoncesco.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "CommunityResponse DTO", description = "커뮤니티 조회")
public class CommunityResponse {

  @Schema(description = "커뮤니티 고유번호", example = "1")
  private long communityId;

  @Schema(description = "도로명코드", example = "117104169350")
  private String roadCode;

  @Schema(description = "건물본번", example = "29")
  private String buildingNumber;
}

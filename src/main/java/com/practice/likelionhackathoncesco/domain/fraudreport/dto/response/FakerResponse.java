package com.practice.likelionhackathoncesco.domain.fraudreport.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "FakerResponseDTO", description = "신고당한 임대인 정보 저장 응답 DTO")
public class FakerResponse {

  @Schema(description = "사기 임대인 이름", example = "이완용")
  private String fakerName;

  @Schema(description = "사기 임대인 주민번호 앞자리", example = "630409")
  private String residentNum;
}

package com.practice.likelionhackathoncesco.infra.openai.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GptOwnerListResponse { // Wrapper DTO
  private List<FakerInfo> faker;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FakerInfo {
    private String fakerName;
    private String residentNum;
  }
}

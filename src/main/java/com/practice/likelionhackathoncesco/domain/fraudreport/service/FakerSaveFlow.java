package com.practice.likelionhackathoncesco.domain.fraudreport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.FakerResponse;
import com.practice.likelionhackathoncesco.infra.openai.dto.response.GptComplaintResponse;
import com.practice.likelionhackathoncesco.infra.openai.service.GptComplaintService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FakerSaveFlow {

  private final GptComplaintService gptComplaintService;

  public List<FakerResponse> processSaveFakerInfo(Long fraudRegisterReportId) {

    List<Map<String, String>> prompts;
    try {
      prompts = gptComplaintService.createGetFakerPrompt(fraudRegisterReportId);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      prompts = new ArrayList<>();
    }

    // gpt-4o api 호출
    String content = gptComplaintService.callGptAPI(prompts, String.valueOf(fraudRegisterReportId));

    // 응답 파싱
    List<GptComplaintResponse> list = gptComplaintService.parseGptComplaintResponseList(content);

    // DB에 저장
    return gptComplaintService.saveFakerInfo(list, fraudRegisterReportId);
  }
}

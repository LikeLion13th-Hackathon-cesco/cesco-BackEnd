package com.practice.likelionhackathoncesco.domain.analysisreport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.AnalysisReportResponse;
import com.practice.likelionhackathoncesco.openai.dto.request.GptAnalysisRequest;
import com.practice.likelionhackathoncesco.openai.dto.response.GptResponse;
import com.practice.likelionhackathoncesco.openai.service.GptService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisFlowService {

  private final GptService gptService;
  private final AnalysisReportService analysisReportService;


  public AnalysisReportResponse processAnalysisReport(Long reportId, GptAnalysisRequest gptAnalysisRequest) {

    // 프롬프트 제작
    List<Map<String, String>> prompts;

    try{
      prompts = gptService.createPrompt(gptAnalysisRequest, reportId);
    }catch (JsonProcessingException e){
      e.printStackTrace();
      prompts = new ArrayList<>();
    }
    
    // gpt-4o api 호출
    String content = gptService.callGptAPI(prompts, String.valueOf(reportId));
    
    // 응답 파싱
    GptResponse gptResponse = gptService.parseGptResponse(content);

    // 분석 리포트 업데이트
    AnalysisReportResponse analysisReportResponse = analysisReportService.updateAnalysisReport(gptResponse,gptAnalysisRequest, reportId);

    return analysisReportResponse;
  }

}

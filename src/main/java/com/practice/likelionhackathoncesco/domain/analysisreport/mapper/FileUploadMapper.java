package com.practice.likelionhackathoncesco.domain.analysisreport.mapper;

import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import org.springframework.stereotype.Component;

@Component
public class FileUploadMapper {
  public FileUploadResponse toFileUploadResponse(AnalysisReport analysisReport) {
    return FileUploadResponse.builder()
        .fileName(analysisReport.getFileName())
        .processingStatus(analysisReport.getProcessingStatus())
        .build();
  }
}

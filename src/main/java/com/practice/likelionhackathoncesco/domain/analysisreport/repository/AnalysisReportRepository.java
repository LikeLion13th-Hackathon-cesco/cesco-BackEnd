package com.practice.likelionhackathoncesco.domain.analysisreport.repository;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {
  //List<AnalysisReport> findById(Long reportId);

}

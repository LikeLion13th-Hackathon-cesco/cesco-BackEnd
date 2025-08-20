package com.practice.likelionhackathoncesco.domain.analysisreport.repository;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {
  List<AnalysisReport> findAllByOrderByCreatedAtDesc(); // 등기부등본 최신순으로 조회

  List<AnalysisReport> findAllByUserUserId(Long userId); // 사용자 id로 분석레포트 전체 조회

  int countAllByUserUserId(Long userId); // 사용자 id로 분석레포트 개수 조회

  AnalysisReport findByUserUserId(Long reportId); // 분석 리포트 id로 분석리포트 조회
}

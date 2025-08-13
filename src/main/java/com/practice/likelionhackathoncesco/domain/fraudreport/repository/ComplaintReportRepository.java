package com.practice.likelionhackathoncesco.domain.fraudreport.repository;

import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ComplaintReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintReportRepository extends JpaRepository<ComplaintReport, Long> {

}

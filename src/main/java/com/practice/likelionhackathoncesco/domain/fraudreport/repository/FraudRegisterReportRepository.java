package com.practice.likelionhackathoncesco.domain.fraudreport.repository;

import com.practice.likelionhackathoncesco.domain.fraudreport.entity.FraudRegisterReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudRegisterReportRepository extends JpaRepository<FraudRegisterReport, Long> {

}

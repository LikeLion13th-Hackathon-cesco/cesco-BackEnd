package com.practice.likelionhackathoncesco.domain.fraudreport.repository;

import com.practice.likelionhackathoncesco.domain.fraudreport.entity.Faker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FakerRepository extends JpaRepository<Faker, Long> {
  Boolean existsByFakerNameAndResidentNum(String fakerName, String residentNum);
}

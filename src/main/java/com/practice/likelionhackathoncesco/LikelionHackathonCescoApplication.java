package com.practice.likelionhackathoncesco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing // JPA가 알아서 엔티티가 생성, 수정될 때의 시간을 자동으로 넣어줌
@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
public class LikelionHackathonCescoApplication {

  public static void main(String[] args) {
    SpringApplication.run(LikelionHackathonCescoApplication.class, args);
  }
}

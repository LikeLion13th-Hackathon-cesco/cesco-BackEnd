package com.practice.likelionhackathoncesco.domain.user.repository;

import com.practice.likelionhackathoncesco.domain.user.entity.PayStatus;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);

  // 현재 시간 기준으로 결제 만료일이 지났지만 PAID 상태인 사용자 조회 (잘못된 상태임)
  List<User> findByPayStatusAndExpirationDateBefore(PayStatus payStatus, LocalDateTime dateTime);
}

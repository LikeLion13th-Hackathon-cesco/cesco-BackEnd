package com.practice.likelionhackathoncesco.domain.user.repository;

import com.practice.likelionhackathoncesco.domain.user.entity.PayStatus;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);

  // 현재 시간 기준으로 결제 만료일이 지났지만 PAID 상태인 사용자 조회 (잘못된 상태임)
  List<User> findByPayStatusAndExpirationDateBefore(PayStatus payStatus, LocalDateTime dateTime);

  // 결제 상태가 만료인 사용자 조회 (u는 User엔티티에 대한 별칭)
  @Query("SELECT u FROM User u WHERE u.payStatus = 'PAID' AND u.expirationDate < :currentTime")
  List<User> findExpiredPaidUsers(@Param("currentTime") LocalDateTime currentTime);
}

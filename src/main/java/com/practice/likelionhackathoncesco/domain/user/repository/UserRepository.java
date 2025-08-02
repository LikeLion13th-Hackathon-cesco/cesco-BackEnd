package com.practice.likelionhackathoncesco.domain.user.repository;

import com.practice.likelionhackathoncesco.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Boolean existsByUsername(String username);
  Optional<User> findByUsername(String username);
}

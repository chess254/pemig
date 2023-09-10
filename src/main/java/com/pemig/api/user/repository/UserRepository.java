package com.pemig.api.user.repository;

import com.pemig.api.user.model.User;
import com.pemig.api.util.logger.Logs;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
@Logs
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}

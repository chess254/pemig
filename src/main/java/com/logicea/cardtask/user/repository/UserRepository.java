package com.logicea.cardtask.user.repository;

import com.logicea.cardtask.user.model.User;
import com.logicea.cardtask.util.logger.Logs;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


@Logs
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}

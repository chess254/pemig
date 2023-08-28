package com.logicea.cardtask.auth.service;

import static com.logicea.cardtask.util.Const.ADMIN_AUTHORITY;
import static com.logicea.cardtask.util.Const.MEMBER_AUTHORITY;

import com.logicea.cardtask.auth.controller.JwtAuthController;
import com.logicea.cardtask.user.model.UserDto;
import com.logicea.cardtask.user.model.User;
import com.logicea.cardtask.user.model.Role;
import com.logicea.cardtask.user.repository.UserRepository;
import com.logicea.cardtask.util.JwtReqFilter;
import com.logicea.cardtask.util.exceptions.EmailExistsException;
import com.logicea.cardtask.util.logger.Logs;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class that talks to the database to retrieve and store user information.
 *
 * @author caleb
 * @see JwtReqFilter
 * @see JwtAuthService
 * @see JwtAuthController
 */
@Service
@RequiredArgsConstructor
@Logs
public class JwtUserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isPresent()) {
      return new org.springframework.security.core.userdetails.User(
          user.get().getEmail(),
          user.get().getPassword(),
          Collections.singletonList(
              user.get().getRole() == Role.ADMIN ? ADMIN_AUTHORITY : MEMBER_AUTHORITY));
    } else {
      throw new UsernameNotFoundException("User with email: " + email + " not found.");
    }
  }


  public UserDto save(UserDto newUser) throws EmailExistsException {
    try {
      User savedUser =
          userRepository.save(
              new User(
                  newUser.getEmail().trim(),
                  encoder.encode(newUser.getPassword()),
                  newUser.getRole()));
      return new UserDto(savedUser.getEmail(), savedUser.getPassword(), savedUser.getRole());
    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new EmailExistsException(newUser.getEmail().trim());
    }
  }
}

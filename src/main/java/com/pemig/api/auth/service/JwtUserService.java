package com.pemig.api.auth.service;

import com.pemig.api.auth.controller.JwtAuthController;
import com.pemig.api.user.model.UserDto;
import com.pemig.api.user.model.User;
import com.pemig.api.user.model.Role;
import com.pemig.api.user.repository.UserRepository;
import com.pemig.api.util.JwtReqFilter;
import com.pemig.api.util.exceptions.EmailExistsException;
import com.pemig.api.util.logger.Logs;
import java.util.Collections;
import java.util.Optional;

import com.pemig.api.util.Const;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
//@NoArgsConstructor
@Logs
public class JwtUserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder encoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isPresent()) {
      return new org.springframework.security.core.userdetails.User(
          user.get().getEmail(),
          user.get().getPassword(),
          //TODO: check how to assign multiple authorities, and what authorizations those authorities would have perhaps use switch-case.
          Collections.singletonList(
              user.get().getRole() == Role.ADMIN ? Const.ADMIN_AUTHORITY : Const.MEMBER_AUTHORITY));
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

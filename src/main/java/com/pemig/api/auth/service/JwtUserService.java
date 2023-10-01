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
              user.get().getRole() == Role.ADMIN ? Const.ADMIN_AUTHORITY : Const.MEMBER_AUTHORITY)
      );

//      CLIENT,
//              CLIENT_MANAGER,
//              ACCOUNT_MANAGER,
//              PAYMENT_ADMIN,
//              REPORTING_AND_ANALYTICS_ADMIN,
//              LOAN_AGENT_ADMIN
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
                  newUser.getRole(), newUser.getFirst_name(), newUser.getMiddle_name(), newUser.getLast_name(), newUser.getIdNo(), newUser.getBirth_date()
              )
//                  User.builder().email(newUser.getEmail().trim()).password(encoder.encode(newUser.getPassword())).role(newUser.getRole())
//                          .firstName(newUser.getFirst_name()).middleName(newUser.getMiddle_name()).lastName(newUser.getLast_name()).idNo(newUser.getIdNo()).build()
          );
        return UserDto.builder().first_name(savedUser.getFirstName()).last_name(savedUser.getLastName()).middle_name(savedUser.getMiddleName())
                .idNo(savedUser.getIdNo()).email(savedUser.getEmail()).password(savedUser.getPassword()).role(savedUser.getRole()).build();

    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new EmailExistsException(newUser.getEmail().trim());
    }
  }
}

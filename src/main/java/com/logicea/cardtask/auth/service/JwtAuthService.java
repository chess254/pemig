package com.logicea.cardtask.auth.service;

import com.logicea.cardtask.util.logger.Logs;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * A service class that provides a single authentication method for users.
 *
 * @author caleb
 * @see #authenticate(String, String)
 */
@Service
@RequiredArgsConstructor
@Logs
public class JwtAuthService {

  private final AuthenticationManager authenticationManager;

  public void authenticate(String username, String password)
      throws DisabledException, BadCredentialsException {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
  }
}

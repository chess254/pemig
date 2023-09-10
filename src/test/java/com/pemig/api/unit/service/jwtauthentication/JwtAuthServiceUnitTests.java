package com.pemig.api.unit.service.jwtauthentication;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.pemig.api.auth.service.JwtAuthService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Unit tests for {@link JwtAuthService}. Perform extensive use of Mockito and jUnit4 assertions.
 * 
 * @author caleb
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtAuthServiceUnitTests {
  private static final String USERNAME = RandomStringUtils.randomAlphanumeric(10);
  private static final String ACCEPTABLE_PASSWORD = RandomStringUtils.randomAlphanumeric(20);
  private static final UsernamePasswordAuthenticationToken OK_TOKEN =
      new UsernamePasswordAuthenticationToken(new Object(), new Object());
  @InjectMocks private JwtAuthService jwtAuthService;
  @Mock private AuthenticationManager authenticationManager;

  @Test
  public void whenAuthenticationManagerAuthenticates_thenAllOk() {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(OK_TOKEN);
    Exception exc = null;
    try {
      jwtAuthService.authenticate(USERNAME, ACCEPTABLE_PASSWORD);
    } catch (Exception thrown) {
      exc = thrown;
    }
    assertNull(exc, "Did not expect authentication method to throw.");
  }

  @Test(expected = Exception.class)
  public void whenAuthenticationManagerThrowsDisabledException_thenThrowException() {
    doThrow(new DisabledException("some message"))
        .when(authenticationManager)
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    jwtAuthService.authenticate(USERNAME, ACCEPTABLE_PASSWORD);
  }

  @Test(expected = Exception.class)
  public void whenAuthenticationManagerThrowsBadCredentialsException_thenThrowException(){
    doThrow(new BadCredentialsException("some message"))
        .when(authenticationManager)
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    jwtAuthService.authenticate(USERNAME, ACCEPTABLE_PASSWORD);
  }
}

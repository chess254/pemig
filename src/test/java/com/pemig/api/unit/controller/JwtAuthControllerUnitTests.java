package com.pemig.api.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.pemig.api.auth.controller.JwtAuthController;
import com.pemig.api.auth.JwtReq;
import com.pemig.api.auth.JwtResp;
import com.pemig.api.user.model.UserDto;
import com.pemig.api.user.model.Role;
import com.pemig.api.auth.service.JwtAuthService;
import com.pemig.api.auth.service.JwtUserService;
import com.pemig.api.util.JwtUtil;
import com.pemig.api.util.TestUserDetailsImpl;
import java.util.Objects;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link JwtAuthController}. Make extensive use of Mockito and jUnit4 assertions.
 *
 * @author caleb
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtAuthControllerUnitTests {

  private static final TestUserDetailsImpl TEST_USER_DETAILS =
      new TestUserDetailsImpl("username", "password");
  private static final JwtReq TEST_JWT_REQUEST = new JwtReq("username", "password");
  @InjectMocks private JwtAuthController jwtAuthController;
  @Mock private JwtUtil jwtUtil;
  @Mock private JwtUserService userDetailsService;
  @Mock private JwtAuthService jwtAuthService;

  @Test
  public void whenUserIsAuthenticatedInDB_thenReturnNewToken() throws Exception {
    doNothing().when(jwtAuthService).authenticate(anyString(), anyString());
    when(userDetailsService.loadUserByUsername(anyString())).thenReturn(TEST_USER_DETAILS);
    when(jwtUtil.generateToken(TEST_USER_DETAILS)).thenReturn("token");
    Assertions.assertEquals(
        Objects.requireNonNull(
            jwtAuthController.authenticate(TEST_JWT_REQUEST).getBody()),
        new JwtResp("token"));
  }

  @Test
  public void whenUserRegistersWithAUsernameWithLeadingAndTrailingWhitespace_thenReturnedUserDetailsHasTheUsernameTrimmed(){
    UserDto userDto = new UserDto(" max    ", "maxpassword",  Role.ADMIN);
    UserDto expectedUserDto = new UserDto("max" , "maxpassword", Role.ADMIN); // The controller does not actually ever return the password, but that's fine for this unit test.
    when(userDetailsService.save(userDto)).thenAnswer(invocationOnMock -> { 
        UserDto providedUserDto = invocationOnMock.getArgument(0);
        return new UserDto(providedUserDto.getEmail().trim(), providedUserDto.getPassword(), providedUserDto.getRole());
      });
    assertEquals(new ResponseEntity<>(expectedUserDto, HttpStatus.CREATED), 
            jwtAuthController.registerUser(userDto));
  }
}

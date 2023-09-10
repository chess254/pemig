package com.pemig.api.unit.service.jwtauthentication;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.pemig.api.auth.service.JwtUserService;
import com.pemig.api.user.model.UserDto;
import com.pemig.api.user.model.User;
import com.pemig.api.user.model.Role;
import com.pemig.api.user.repository.UserRepository;

import java.util.Optional;

import com.pemig.api.util.Const;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for {@link JwtUserService}. Perform extensive use of Mockito and jUnit4 assertions.
 * 
 * @author caleb
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtUserServiceUnitTests {

  public static final String ADMIN_EMAIL = "admin@company.com";
  public static final String ADMIN_PASSWORD = "adminpass";

  public static final String MEMBER_EMAIL = "plainoljoe@company.com";
  public static final String MEMBER_PASSWORD = "plainoljoepass";
  private static final User TEST_ADMIN_USER_ENTITY = new User(ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
  private static final UserDto TEST_ADMIN_USER_DTO = new UserDto(ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
  private static final User TEST_MEMBER_USER_ENTITY = new User(MEMBER_EMAIL, MEMBER_PASSWORD, Role.MEMBER);
  private static final UserDto TEST_MEMBER_USER_DTO = new UserDto(MEMBER_EMAIL, MEMBER_PASSWORD, Role.MEMBER);
  @InjectMocks private JwtUserService jwtUserService;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @Test
  public void whenAdminUserIsInDB_thenAdminUserDetailsReturned() {
    when(userRepository.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(TEST_ADMIN_USER_ENTITY));
    UserDetails userDetails = jwtUserService.loadUserByUsername(ADMIN_EMAIL);
    assertEquals(userDetails.getUsername(), ADMIN_EMAIL);
    assertEquals(userDetails.getPassword(), ADMIN_PASSWORD);
    Assert.assertEquals(CollectionUtils.extractSingleton(userDetails.getAuthorities()), Const.ADMIN_AUTHORITY);
  }

  @Test
  public void whenMemberUserIsInDB_thenMemberUserDetailsReturned() {
    when(userRepository.findByEmail(MEMBER_EMAIL)).thenReturn(Optional.of(TEST_MEMBER_USER_ENTITY));
    UserDetails userDetails = jwtUserService.loadUserByUsername(MEMBER_EMAIL);
    assertEquals(userDetails.getUsername(), MEMBER_EMAIL);
    assertEquals(userDetails.getPassword(), MEMBER_PASSWORD);
    Assert.assertEquals(CollectionUtils.extractSingleton(userDetails.getAuthorities()), Const.MEMBER_AUTHORITY);
  }

  @Test(expected = UsernameNotFoundException.class)
  public void whenUserIsNotInDB_thenUsernameNotFoundExceptionIsThrown() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    jwtUserService.loadUserByUsername(RandomStringUtils.randomAlphanumeric(10));
  }

  @Test
  public void whenSavingNewAdminUser_thenTheirInformationIsReturned() {
    when(passwordEncoder.encode(any(CharSequence.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0)); // Encoder basically does nothing.
    when(userRepository.save(any())).thenReturn(TEST_ADMIN_USER_ENTITY);
    assertEquals(TEST_ADMIN_USER_DTO, jwtUserService.save(TEST_ADMIN_USER_DTO));
  }

  @Test
  public void whenSavingNewMemberUser_thenTheirInformationIsReturned() {
    when(passwordEncoder.encode(any(CharSequence.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0)); // Encoder basically does nothing.
    when(userRepository.save(any())).thenReturn(TEST_MEMBER_USER_ENTITY);
    assertEquals(TEST_MEMBER_USER_DTO, jwtUserService.save(TEST_MEMBER_USER_DTO));
  }
  
  @Test
  public void whenSavingNewUserWithTrailingAndLeadingWhitespaceInUsername_thenThatUsernameIsTrimmed(){
    UserDto userDto = new UserDto(" max    ", "maxpassword", Role.MEMBER);
    UserDto expectedUserDto = new UserDto("max" , "maxpassword", Role.MEMBER);
    when(passwordEncoder.encode(any(CharSequence.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    Assertions.assertEquals(expectedUserDto, jwtUserService.save(userDto));
  }
}

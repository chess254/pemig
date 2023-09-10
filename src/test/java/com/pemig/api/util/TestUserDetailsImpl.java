package com.pemig.api.util;

import com.pemig.api.unit.controller.JwtAuthControllerUnitTests;

import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A concrete implementation of {@link UserDetails} to assist {@link JwtAuthControllerUnitTests}.
 * 
 * @author caleb
 */
@AllArgsConstructor
@Getter
public class TestUserDetailsImpl implements UserDetails {

  private final String username;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}

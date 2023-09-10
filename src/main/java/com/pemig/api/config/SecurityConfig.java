package com.pemig.api.config;

import com.pemig.api.auth.service.JwtUserService;
import com.pemig.api.util.JwtReqFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final UserDetailsService jwtUserDetailsService;
  private final JwtReqFilter jwtReqFilter;
  private final PasswordEncoder passwordEncoder;

  /**
   * Define {@link AuthenticationManager bean}.
   *
   * @param httpSecurity A {@link HttpSecurity} instance.
   * @return A {@link AuthenticationManager} instance that knows which service to call for obtaining
   *     {@link UserDetails} and is aware of the password encryption scheme.
   * @throws Exception If the {@link AuthenticationManagerBuilder} used underneath throws it while
   *     setting the {@link UserDetailsService} to use.
   * @see UserDetailsService
   * @see JwtUserService
   */
  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder
        .userDetailsService(jwtUserDetailsService)
        .passwordEncoder(passwordEncoder);
    return authenticationManagerBuilder.build();
  }

  /**
   * Define the {@link SecurityFilterChain}bean.
   *
   * @param http An instance of {@link HttpSecurity}
   * @return A fully defined {@link SecurityFilterChain} with endpoints to permit without
   *     authentication, defined authentication entry point, session creation policy, etc.
   * @throws Exception If {@link HttpSecurity#build()} throws it.
   * @see SecurityFilterChain
   */
  @SuppressWarnings({"deprecated", "removal"})
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .requestMatchers(
//                new MvcRequestMatcher(new HandlerMappingIntrospector(), Arrays.asList("/cardsapi/register",
//                        "/cardsapi/authenticate",
//                        "/swagger-ui-custom.html",
//                        "/swagger-ui.html",
//                        "/swagger-ui/**",
//                        "/v3/api-docs/**",
//                        "/webjars/**",
//                        "/swagger-ui/index.html",
//                        "/api-docs/**").toString())
                "/pemig/register",
                "/pemig/authenticate",
            "/swagger-ui-custom.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-ui/index.html",
            "/api-docs/**",
            "/h2-console/**"
        )
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.addFilterBefore(jwtReqFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}

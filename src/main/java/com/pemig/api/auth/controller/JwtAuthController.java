package com.pemig.api.auth.controller;

import com.pemig.api.auth.service.JwtAuthService;
import com.pemig.api.auth.JwtReq;
import com.pemig.api.auth.JwtResp;
import com.pemig.api.auth.service.JwtUserService;
import com.pemig.api.user.model.UserDto;
import com.pemig.api.util.JwtUtil;
import com.pemig.api.util.logger.Logs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user authentication. Provides endpoints for authentication and login.
 *
 * @author caleb
 */
@RestController
@RequestMapping("/pemig")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "1. Authentication API")
@Validated
@Logs
public class JwtAuthController {

  private final JwtUtil jwtUtil;
  private final JwtUserService userDetailsService;
  private final JwtAuthService jwtAuthService;

  @Operation(summary = "Authenticate with your username and password")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful, JWT returned.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = JwtResp.class))
            }),
        @ApiResponse(responseCode = "401", description = "Bad password.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Username not found.", content = @Content)
      })
  @PostMapping(value = "/authenticate")
  public ResponseEntity<JwtResp> authenticate(
      @RequestBody @Valid JwtReq authenticationRequest) {

    jwtAuthService.authenticate(
        authenticationRequest.getEmail(), authenticationRequest.getPassword());
    final UserDetails userDetails =
        userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
    final String token = jwtUtil.generateToken(userDetails);
    return ResponseEntity.ok(new JwtResp(token));
  }


  @Operation(summary = "Register with your username and password")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Registration successful.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                "Invalid password length provided; passwords should be from 8 to 30 characters.",
            content = @Content),
        @ApiResponse(
            responseCode = "409",
            description = "Username already taken.",
            content = @Content)
      })
  @PostMapping(value = "/register")
  public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserDto user) {
    return new ResponseEntity<>(userDetailsService.save(user), HttpStatus.CREATED);
  }
}

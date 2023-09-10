package com.pemig.api.util.exceptions;

import lombok.Getter;


@Getter
public class UnauthorizedException extends RuntimeException {

  private final String username;

  public UnauthorizedException(String user) {
    super("User " + user + " is unauthorized to perform this action.");
    this.username = user;
  }
}

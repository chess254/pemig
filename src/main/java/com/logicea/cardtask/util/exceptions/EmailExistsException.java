package com.logicea.cardtask.util.exceptions;

import lombok.Getter;

/**
 * A {@link RuntimeException} thrown when the user tries to register with a username that already
 * exists in the database.
 *
 * @author caleb
 */
@Getter
public class EmailExistsException extends RuntimeException {

  private final String username;

  public EmailExistsException(String username) {
    super("Username " + username + " already registered.");
    this.username = username;
  }
}

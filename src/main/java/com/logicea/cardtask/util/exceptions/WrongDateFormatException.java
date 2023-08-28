package com.logicea.cardtask.util.exceptions;

/**
 * A {@link RuntimeException} thrown in cases of a bad date format provided by the user.
 *
 * @author caleb
 */
public class WrongDateFormatException extends RuntimeException {

  public WrongDateFormatException(String msg) {
    super(msg);
  }
}

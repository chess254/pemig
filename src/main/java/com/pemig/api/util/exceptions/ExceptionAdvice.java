package com.pemig.api.util.exceptions;

import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ExceptionAdvice {

  @ResponseBody
  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentNotValidException.class,
    WrongSortFieldException.class,
    MethodArgumentTypeMismatchException.class,
    WrongDateFormatException.class,
    HibernateException.class,
    LoanNameNotValidException.class,
    CardNameBlankException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ExceptionWrapper> badRequestStatusMessage(Exception exc) {
    return new ResponseEntity<>(new ExceptionWrapper(exc.getMessage()), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handler for all exceptions that should return an HTTP Status Code of {@link HttpStatus#UNAUTHORIZED}.
   * @param exc The {@link Exception} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#UNAUTHORIZED} as the status code.
   */
  @ResponseBody
  @ExceptionHandler({
      BadCredentialsException.class,
  })
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ExceptionWrapper> unauthorizedStatusMessage(Exception exc) {
    return new ResponseEntity<>(new ExceptionWrapper(exc.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  /**
   * Handler for all exceptions that should return an HTTP Status code of {@link HttpStatus#CONFLICT}
   * @param exc he {@link Exception} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#CONFLICT} as the status code.
   *
   */
  @ResponseBody
  @ExceptionHandler({EmailExistsException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  private ResponseEntity<ExceptionWrapper> conflictStatusMessage(Exception exc) {
    return new ResponseEntity<>(new ExceptionWrapper(exc.getMessage()), HttpStatus.CONFLICT);
  }

  /**
   * Handler for all exceptions that should return an HTTP Status Code of {@link HttpStatus#NOT_FOUND}.
   * @param exc The {@link Exception} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#NOT_FOUND} as the status code.
   */
  @ResponseBody
  @ExceptionHandler({UsernameNotFoundException.class, LoanNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ExceptionWrapper> notFoundStatusMessage(Exception exc) {
    return new ResponseEntity<>(new ExceptionWrapper(exc.getMessage()), HttpStatus.NOT_FOUND);
  }

  /**
   * Handler for all exceptions that should return an HTTP Status Code of {@link HttpStatus#FORBIDDEN}.
   * @param exc The {@link Exception} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#FORBIDDEN} as the status code.
   */
  @ResponseBody
  @ExceptionHandler({
          UnauthorizedException.class,
  })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<ExceptionWrapper> forbiddenStatusMessage(Exception exc) {
    return new ResponseEntity<>(new ExceptionWrapper(exc.getMessage()), HttpStatus.FORBIDDEN);
  }
}

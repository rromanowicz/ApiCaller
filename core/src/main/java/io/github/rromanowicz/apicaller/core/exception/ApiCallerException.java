package io.github.rromanowicz.apicaller.core.exception;

/**
 * Base ApiCaller exception.
 */
public class ApiCallerException extends RuntimeException {

  public ApiCallerException(String message) {
    super(message);
  }

  public ApiCallerException(String message, Exception e) {
    super(message, e);
  }

}

package io.github.rromanowicz.apicaller.core.exception;

/**
 * {@code ApiCallerException} wrapper for rest 3xx response codes.
 */
public class Api3xxException extends ApiCallerException {

  public Api3xxException(String message) {
    super(message);
  }

  public Api3xxException(String message, Exception e) {
    super(message, e);
  }
}

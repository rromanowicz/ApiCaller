package io.github.rromanowicz.apicaller.core.exception;

/**
 * {@code ApiCallerException} wrapper for rest 5xx response codes.
 */
public class Api5xxException extends ApiCallerException {

  public Api5xxException(String message) {
    super(message);
  }

  public Api5xxException(String message, Exception e) {
    super(message, e);
  }
}

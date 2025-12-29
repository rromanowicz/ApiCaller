package io.github.rromanowicz.apicaller.core.exception;

/**
 * {@code ApiCallerException} wrapper for rest 4xx response codes.
 */
public class Api4xxException extends ApiCallerException {

  public Api4xxException(String message) {
    super(message);
  }

  public Api4xxException(String message, Exception e) {
    super(message, e);
  }
}

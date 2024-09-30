package com.navayug_newspaper.Navayug.exception;

public class InvalidAPIKeyException extends RuntimeException {
  public InvalidAPIKeyException(String message) {
    super(message);
  }

  public InvalidAPIKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}

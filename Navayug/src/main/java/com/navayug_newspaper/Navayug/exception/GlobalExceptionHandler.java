package com.navayug_newspaper.Navayug.exception;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleException(Exception ex) {
    log.error("Exception message", ex.getMessage());
    Map<String,Object> body = new HashMap<>();
    body.put("message", "Internal Server Error");
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body);
  }

  @ExceptionHandler({InvalidAPIKeyException.class})
  public ResponseEntity<Object> handleInvalidAPIKey(Exception ex) {
    log.error("Exception message", ex.getMessage());
    Map<String,Object> body = new HashMap<>();
    body.put("message", "Invalid authentication credentials");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @ExceptionHandler(InvalidParameterException.class)
  public ResponseEntity<Object> handleInvalidRequestExceptionException(Exception ex) {
    log.error("Invalid request parameter value", ex);
    Map<String,Object> body = new HashMap<>();
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(body);
  }
}

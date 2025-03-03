package com.be_notemasterai.exception;

import static com.be_notemasterai.exception.ErrorCode.INTERNAL_SERVER_ERROR;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

    ErrorCode errorCode = e.getErrorCode();
    ErrorResponse response = ErrorResponse.from(errorCode);

    log.error("Custom Exception: {}, Code: {}", errorCode.getMessage(), errorCode.getHttpStatus());

    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {

    ErrorResponse response = ErrorResponse.from(INTERNAL_SERVER_ERROR);

    log.error("Unexpected: {}", e.getMessage(), e);

    return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
  }
}
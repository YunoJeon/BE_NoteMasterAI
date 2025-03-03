package com.be_notemasterai.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // 400 BAD REQUEST

  // 401 UNAUTHORIZED

  // 403 FORBIDDEN

  // 404 NOT FOUND

  // 408 REQUEST TIMEOUT

  // 409 CONFLICT

  // 500 INTERNAL SERVER ERROR
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
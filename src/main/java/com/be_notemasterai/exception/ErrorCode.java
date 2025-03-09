package com.be_notemasterai.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // 400 BAD REQUEST
  INVALID_PROVIDER(BAD_REQUEST, "지원하지 않는 소셜 로그인입니다."),
  ALREADY_SET_NICKNAME(BAD_REQUEST, "이미 닉네임이 설정되었습니다."),
  // 401 UNAUTHORIZED
  INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  INVALID_REFRESH_TOKEN(UNAUTHORIZED, "리프레쉬 토큰이 유효하지 않습니다."),
  // 403 FORBIDDEN

  // 404 NOT FOUND
  NOT_FOUND_MEMBER(NOT_FOUND, "회원 정보가 없습니다."),
  // 408 REQUEST TIMEOUT

  // 409 CONFLICT
  EXISTS_NICKNAME(CONFLICT, "이미 존재하는 닉네임 입니다."),
  // 500 INTERNAL SERVER ERROR
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
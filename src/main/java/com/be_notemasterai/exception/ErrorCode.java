package com.be_notemasterai.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
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
  EMPTY_SOUND_FILE(BAD_REQUEST, "음성파일이 업로드 되지 않았습니다."),
  STATUS_NOT_PAID(BAD_REQUEST, "결제 상태가 paid 가 아닙니다."),
  INVALID_AMOUNT(BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
  // 401 UNAUTHORIZED
  INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  INVALID_REFRESH_TOKEN(UNAUTHORIZED, "리프레쉬 토큰이 유효하지 않습니다."),
  // 403 FORBIDDEN
  NOT_SUBSCRIBER(FORBIDDEN, "구독자만 접근 가능합니다."),
  PAYMENT_OWNER_MISMATCH(FORBIDDEN, "결제정보가 일치하지 않습니다."),
  // 404 NOT FOUND
  NOT_FOUND_MEMBER(NOT_FOUND, "회원 정보가 없습니다."),
  NOT_FOUND_PAYMENT(NOT_FOUND, "결제 정보가 없습니다."),
  NOT_FOUND_SUBSCRIPTION(NOT_FOUND, "구독 내역을 확인할 수 없습니다."),
  // 408 REQUEST TIMEOUT

  // 409 CONFLICT
  EXISTS_NICKNAME(CONFLICT, "이미 존재하는 닉네임 입니다."),
  ALREADY_REFUNDED(CONFLICT, "이미 환불되었습니다."),
  // 500 INTERNAL SERVER ERROR
  FAILED_VALID_PAYMENT(HttpStatus.INTERNAL_SERVER_ERROR, "결제 검증에 실패하였습니다."),
  FAILED_REFUND_PAYMENT(HttpStatus.INTERNAL_SERVER_ERROR, "결제 환불에 실패하였습니다."),
  FAILED_TO_STT(HttpStatus.INTERNAL_SERVER_ERROR, "음성 파일 읽는 중 오류가 발생했습니다."),
  FAILED_STT_PARSING(HttpStatus.INTERNAL_SERVER_ERROR, "음성 파일 파싱 중 오류가 발생했습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
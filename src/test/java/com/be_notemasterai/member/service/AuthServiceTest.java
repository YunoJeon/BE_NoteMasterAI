package com.be_notemasterai.member.service;

import static com.be_notemasterai.exception.ErrorCode.INVALID_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.dto.AccessTokenResponse;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.security.jwt.JwtTokenProvider;
import com.be_notemasterai.security.jwt.JwtTokenService;
import com.be_notemasterai.security.oauth2.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private JwtTokenService jwtTokenService;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private CustomOAuth2UserService customOAuth2UserService;

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private AuthService authService;

  private final String refreshToken = "validRefreshToken";
  private final String invalidRefreshToken = "invalidRefreshToken";
  private final String providerUuid = "providerUuid";
  private Member member;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .provider("google")
        .providerUuid(providerUuid)
        .name("이름")
        .nickname("닉네임")
        .profileImageUrl("프로필이미지")
        .tag("tag")
        .build();
  }

  @Test
  @DisplayName("유효한 리프레시 토큰으로 액세스 토큰 재발급에 성공한다")
  void refreshAccessToken_success() {
    // given
    when(jwtTokenProvider.getProviderUuidFromToken(refreshToken)).thenReturn(providerUuid);
    when(customOAuth2UserService.getByProviderUuid(providerUuid)).thenReturn(member);
    when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
    when(jwtTokenService.validateRefreshToken(providerUuid, refreshToken)).thenReturn(true);
    when(jwtTokenProvider.createAccessToken(providerUuid)).thenReturn("newAccessToken");
    // when
    AccessTokenResponse accessTokenResponse = authService.refreshAccessToken(refreshToken);
    // then
    assertEquals("newAccessToken", accessTokenResponse.accessToken());
  }

  @Test
  @DisplayName("유효하지 않는 리프레시 토큰 사용 시 예외가 발생한다")
  void refreshAccessToken_failure_InvalidRefreshToken() {
    // given
    when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> authService.refreshAccessToken(invalidRefreshToken));
    // then
    assertEquals(e.getMessage(), "유효하지 않은 토큰입니다.");
    assertEquals(e.getErrorCode(), INVALID_TOKEN);
  }

  @Test
  @DisplayName("로그아웃 시 Redis 에서 리프레시 토큰 삭제와 쿠기 만료를 시킨다")
  void signOut_success() {
    // given
    when(jwtTokenProvider.getProviderUuidFromToken(refreshToken)).thenReturn(providerUuid);
    when(jwtTokenService.createExpiredCookie("refreshToken")).thenReturn(ResponseCookie.from("refreshToken", "").build());
    // when
    authService.signOut(refreshToken, response);
    // then
    verify(jwtTokenService).deleteRefreshToken(providerUuid);
    verify(response, times(1)).addHeader(eq("Set-Cookie"), anyString());
  }
}
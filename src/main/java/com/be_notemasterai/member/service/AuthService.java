package com.be_notemasterai.member.service;

import static com.be_notemasterai.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.be_notemasterai.exception.ErrorCode.INVALID_TOKEN;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.security.jwt.JwtTokenProvider;
import com.be_notemasterai.security.jwt.JwtTokenService;
import com.be_notemasterai.security.oauth2.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final JwtTokenService jwtTokenService;

  private final JwtTokenProvider jwtTokenProvider;

  private final CustomOAuth2UserService customOAuth2UserService;

  public void refreshAccessToken(String refreshToken, HttpServletResponse response) {

    if (refreshToken == null || refreshToken.isEmpty()) {
      throw new CustomException(INVALID_REFRESH_TOKEN);
    }

    String providerUuid = jwtTokenProvider.getProviderUuidFromToken(refreshToken);

    customOAuth2UserService.getByProviderUuid(providerUuid);

    boolean validateToken = jwtTokenProvider.validateToken(refreshToken);

    if (!validateToken || !jwtTokenService.validateRefreshToken(providerUuid, refreshToken)) {
      throw new CustomException(INVALID_TOKEN);
    }

    String newAccessToken = jwtTokenProvider.createAccessToken(providerUuid);

    response.addHeader("Set-Cookie", jwtTokenService.createAccessCookie(newAccessToken).toString());
  }

  public void signOut(String refreshToken, HttpServletResponse response) {

    if (refreshToken != null) {
      String providerUuid = jwtTokenProvider.getProviderUuidFromToken(refreshToken);

      jwtTokenService.deleteRefreshToken(providerUuid);
    }

    response.addHeader("Set-Cookie", jwtTokenService.createExpiredCookie("accessToken").toString());
    response.addHeader("Set-Cookie", jwtTokenService.createExpiredCookie("refreshToken").toString());
  }
}
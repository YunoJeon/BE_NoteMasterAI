package com.be_notemasterai.security.oauth2;

import static java.util.concurrent.TimeUnit.MINUTES;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.security.jwt.JwtTokenProvider;
import com.be_notemasterai.security.jwt.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenService jwtTokenService;

  private final JwtTokenProvider jwtTokenProvider;

  private static final String FRONTEND_BASE_URL = "http://localhost:5173";
  private static final String DASHBOARD_URI = FRONTEND_BASE_URL + "/dashboard";
  private static final String NICKNAME_SETUP_URI = FRONTEND_BASE_URL + "/nickname-setup";
  private static final String REJOIN_URI = FRONTEND_BASE_URL + "/rejoin";
  private static final long TAG_EXPIRATION = MINUTES.toSeconds(5);

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    String providerUuid = principalDetails.getUsername();

    Member member = principalDetails.member();

    if (member.getDeletedAt() != null) {

      ResponseCookie tagCookie = ResponseCookie.from("tag", member.getTag())
          .httpOnly(true)
          .secure(true)
          .path("/")
          .maxAge(TAG_EXPIRATION)
          .build();

      response.addHeader("Set-Cookie", tagCookie.toString());

      response.sendRedirect(REJOIN_URI);
      return;
    }

    String refreshToken = jwtTokenProvider.createRefreshToken(providerUuid);

    jwtTokenService.setRefreshToken(response, providerUuid, refreshToken);

    String redirectUri = (principalDetails.member().getNickname() == null)
        ? NICKNAME_SETUP_URI
        : DASHBOARD_URI;

    response.sendRedirect(redirectUri);
  }
}
package com.be_notemasterai.security.jwt;

import static java.util.concurrent.TimeUnit.SECONDS;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

  private final StringRedisTemplate redisTemplate;

  private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;

  public void setRefreshTokenFromRedis(String providerUuid, String refreshToken) {

    redisTemplate.opsForValue().set(providerUuid, refreshToken, REFRESH_TOKEN_EXPIRATION, SECONDS);
  }

  public void setRefreshToken(HttpServletResponse response, String providerUuid, String refreshToken) {

    setRefreshTokenFromRedis(providerUuid, refreshToken);

    response.addHeader("Set-Cookie", createRefreshCookie(refreshToken).toString());
  }

  private ResponseCookie createRefreshCookie(String refreshToken) {

    return ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(REFRESH_TOKEN_EXPIRATION)
        .build();
  }

  public String getRefreshToken(String providerUuid) {

    return redisTemplate.opsForValue().get(providerUuid);
  }

  public boolean validateRefreshToken(String providerUuid, String refreshToken) {

    String storedRefreshToken = getRefreshToken(providerUuid);

    return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
  }

  public void deleteRefreshToken(String providerUuid) {
    redisTemplate.delete(providerUuid);
  }

  public ResponseCookie createExpiredCookie(String name) {

    return ResponseCookie.from(name, "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build();
  }
}
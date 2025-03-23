package com.be_notemasterai.security.jwt;

import static com.be_notemasterai.exception.ErrorCode.INVALID_TOKEN;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;

import com.be_notemasterai.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private final Key key;

  private static final long ACCESS_TOKEN_EXPIRATION = MINUTES.toSeconds(5);
  private static final long REFRESH_TOKEN_EXPIRATION = DAYS.toSeconds(7);

  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
    this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
  }

  public String createAccessToken(String providerUuid) {
    return createToken(providerUuid, ACCESS_TOKEN_EXPIRATION);
  }

  public String createRefreshToken(String providerUuid) {
    return createToken(providerUuid, REFRESH_TOKEN_EXPIRATION);
  }

  private String createToken(String providerUuid, long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + (expiration * 1000));

    return Jwts.builder()
        .setSubject(providerUuid)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, HS256)
        .compact();
  }

  public String getProviderUuidFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateToken(String token) {

    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.error("만료된 JWT 토큰입니다.");
      throw new CustomException(INVALID_TOKEN);
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }
}
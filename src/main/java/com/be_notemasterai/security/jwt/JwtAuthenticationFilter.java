package com.be_notemasterai.security.jwt;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.security.oauth2.CustomOAuth2UserService;
import com.be_notemasterai.security.oauth2.PrincipalDetails;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  private final CustomOAuth2UserService customOAuth2UserService;

  @Override
  protected void doFilterInternal(
      @Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain) throws ServletException, IOException {

    String token = resolveToken(request);

    if (token != null) {
      try {
        if (jwtTokenProvider.validateToken(token)) {

          String providerUuid = jwtTokenProvider.getProviderUuidFromToken(token);

          Member member = customOAuth2UserService.getByProviderUuid(providerUuid);

          PrincipalDetails principalDetails = new PrincipalDetails(member, null);

          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              principalDetails, null, principalDetails.getAuthorities());

          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (CustomException e) {
        log.error("🚨 Jwt 인증 실패: {}", e.getMessage());
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter()
            .write("{\"error\":\"INVALID_TOKEN\",\"message\":\"Access Token expired.\"}");
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {

    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
package com.be_notemasterai.member.controller;

import com.be_notemasterai.member.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/refresh")
  public ResponseEntity<Void> refreshAccessToken(
      @CookieValue(value = "refreshToken", required = false) String refreshToken,
      HttpServletResponse response) {

    authService.refreshAccessToken(refreshToken, response);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("sign-out")
  public ResponseEntity<Void> signOut(
      @CookieValue(value = "refreshToken", required = false) String refreshToken,
      HttpServletResponse response) {

    authService.signOut(refreshToken, response);

    return ResponseEntity.ok().build();
  }
}
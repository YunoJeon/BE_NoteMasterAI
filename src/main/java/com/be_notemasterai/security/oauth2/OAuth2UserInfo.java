package com.be_notemasterai.security.oauth2;

import static com.be_notemasterai.exception.ErrorCode.INVALID_PROVIDER;

import com.be_notemasterai.exception.CustomException;
import java.util.Map;
import lombok.Builder;

@Builder
public record OAuth2UserInfo(

    String provider,
    String providerUuid,
    String name,
    String profileImage
) {

  public static OAuth2UserInfo of(String provider, Map<String, Object> oAuth2UserAttributes) {

    return switch (provider) {
      case "google" -> ofGoogle(provider, oAuth2UserAttributes);
      case "kakao" -> ofKakao(provider,oAuth2UserAttributes);
      case "naver" -> ofNaver(provider, oAuth2UserAttributes);
      default -> throw new CustomException(INVALID_PROVIDER);
    };
  }

  private static OAuth2UserInfo ofGoogle(String provider, Map<String, Object> oAuth2UserAttributes) {
    return OAuth2UserInfo.builder()
        .provider(provider)
        .providerUuid(oAuth2UserAttributes.get("sub").toString())
        .name(oAuth2UserAttributes.get("name").toString())
        .profileImage(oAuth2UserAttributes.get("picture").toString())
        .build();
  }

  private static OAuth2UserInfo ofKakao(String provider, Map<String, Object> oAuth2UserAttributes) {

    Map<String, Object> account = (Map<String, Object>) oAuth2UserAttributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) account.get("profile");

    return OAuth2UserInfo.builder()
        .provider(provider)
        .providerUuid(oAuth2UserAttributes.get("id").toString())
        .name(account.get("name").toString())
        .profileImage(profile.get("profile_image_url").toString())
        .build();
  }

  private static OAuth2UserInfo ofNaver(String provider, Map<String, Object> oAuth2UserAttributes) {

    Map<String, Object> response = (Map<String, Object>) oAuth2UserAttributes.get("response");

    return OAuth2UserInfo.builder()
        .provider(provider)
        .providerUuid(response.get("id").toString())
        .name(response.get("name").toString())
        .profileImage(response.get("profile_image").toString())
        .build();
  }
}
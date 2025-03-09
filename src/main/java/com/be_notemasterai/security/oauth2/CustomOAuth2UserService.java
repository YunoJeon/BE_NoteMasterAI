package com.be_notemasterai.security.oauth2;

import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.member.repository.MemberRepository;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

    String provider = userRequest.getClientRegistration().getRegistrationId();

    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(provider, oAuth2UserAttributes);

    Member member = getOrSave(oAuth2UserInfo);

    return new PrincipalDetails(member, oAuth2UserAttributes);
  }

  @Transactional
  public Member getOrSave(OAuth2UserInfo oAuth2UserInfo) {

    return memberRepository.findByProviderUuid(oAuth2UserInfo.providerUuid()).orElseGet(() -> {
      String tag = generateTag();
      return memberRepository.save(Member.of(oAuth2UserInfo, tag));
    });
  }

  public Member getByProviderUuid(String providerUuid) {
    return memberRepository.findByProviderUuid(providerUuid)
        .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));
  }

  private String generateTag() {

    String tag;
    do {
      tag = "@" + generateRandomString();
    } while (memberRepository.existsByTag(tag));
    return tag;
  }

  private String generateRandomString() {

    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < 8; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }
}
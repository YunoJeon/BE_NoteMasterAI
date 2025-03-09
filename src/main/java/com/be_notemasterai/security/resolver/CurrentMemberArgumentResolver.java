package com.be_notemasterai.security.resolver;

import static com.be_notemasterai.exception.ErrorCode.INVALID_TOKEN;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.member.repository.MemberRepository;
import com.be_notemasterai.security.oauth2.PrincipalDetails;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentMemberArgumentResolver implements HandlerMethodArgumentResolver {

  private final MemberRepository memberRepository;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(CurrentMember.class) &&
           parameter.getParameterType().equals(Member.class);
  }

  @Override
  public Object resolveArgument(
      @Nonnull MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    Authentication authentication = (Authentication) webRequest.getUserPrincipal();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new CustomException(INVALID_TOKEN);
    }

    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

    return memberRepository.findByProviderUuid(principalDetails.getUsername())
        .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));
  }
}

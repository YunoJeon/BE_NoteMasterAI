package com.be_notemasterai.security.oauth2;

import com.be_notemasterai.member.entity.Member;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record PrincipalDetails(

    Member member,
    Map<String, Object> attributes

) implements OAuth2User {

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getName() {
    return member.getName();
  }

  public String getUsername() {
    return member.getProviderUuid();
  }
}
package com.be_notemasterai.member.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.be_notemasterai.security.oauth2.OAuth2UserInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "members")
@EntityListeners(AuditingEntityListener.class)
public class Member {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String provider;

  @Column(name = "provider_uuid", nullable = false, unique = true)
  private String providerUuid;

  @Column(nullable = false)
  private String name;

  @Setter
  @Column(unique = true)
  private String nickname;

  @Column(name = "profile_image_url", nullable = false)
  private String profileImageUrl;

  @Column(nullable = false, unique = true)
  private String tag;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  @Setter
  private LocalDateTime deletedAt;

  public static Member of(OAuth2UserInfo oAuth2UserInfo, String tag) {

    return Member.builder()
        .provider(oAuth2UserInfo.provider())
        .providerUuid(oAuth2UserInfo.providerUuid())
        .name(oAuth2UserInfo.name())
        .nickname(null)
        .profileImageUrl(oAuth2UserInfo.profileImage())
        .tag(tag)
        .build();
  }
}
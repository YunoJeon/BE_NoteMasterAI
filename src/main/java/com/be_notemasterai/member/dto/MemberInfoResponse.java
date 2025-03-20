package com.be_notemasterai.member.dto;

import com.be_notemasterai.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberInfoResponse(

    Long memberId,
    String nickname,
    String profileImageUrl
) {

  public static MemberInfoResponse fromEntity(Member getMemberByTag) {

    return MemberInfoResponse.builder()
        .memberId(getMemberByTag.getId())
        .nickname(getMemberByTag.getNickname())
        .profileImageUrl(getMemberByTag.getProfileImageUrl())
        .build();
  }
}
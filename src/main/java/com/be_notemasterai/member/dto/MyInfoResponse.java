package com.be_notemasterai.member.dto;

import com.be_notemasterai.member.entity.Member;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MyInfoResponse(

    String provider,
    String profileImageUrl,
    String nickname,
    String tag,
    LocalDateTime createdAt,
    Long myNotes,
    Long myGroups,
    LocalDateTime subscribeEndedAt
) {

  public static MyInfoResponse fromEntity(Member member, Long countNote, Long countGroups,
      LocalDateTime subscribeEndedAt) {

    return MyInfoResponse.builder()
        .provider(member.getProvider())
        .profileImageUrl(member.getProfileImageUrl())
        .nickname(member.getNickname())
        .tag(member.getTag())
        .createdAt(member.getCreatedAt())
        .myNotes(countNote)
        .myGroups(countGroups)
        .subscribeEndedAt(subscribeEndedAt)
        .build();
  }
}
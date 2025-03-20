package com.be_notemasterai.group.dto;

import com.be_notemasterai.group.entity.GroupMember;
import com.be_notemasterai.group.type.GroupRole;
import lombok.Builder;

@Builder
public record GroupMemberListResponse(

    Long memberId,
    String nickname,
    String profileImageUrl,
    GroupRole groupRole
) {

  public static GroupMemberListResponse fromEntity(GroupMember groupMember) {

    return GroupMemberListResponse.builder()
        .memberId(groupMember.getMember().getId())
        .nickname(groupMember.getMember().getNickname())
        .profileImageUrl(groupMember.getMember().getProfileImageUrl())
        .groupRole(groupMember.getGroupRole())
        .build();
  }
}
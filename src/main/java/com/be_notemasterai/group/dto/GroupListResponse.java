package com.be_notemasterai.group.dto;

import com.be_notemasterai.group.entity.Group;
import lombok.Builder;

@Builder
public record GroupListResponse(

    Long groupId,
    String groupName,
    Long memberCount
) {

  public static GroupListResponse fromEntity(Group group, long memberCount) {

    return GroupListResponse.builder()
        .groupId(group.getId())
        .groupName(group.getGroupName())
        .memberCount(memberCount)
        .build();
  }
}
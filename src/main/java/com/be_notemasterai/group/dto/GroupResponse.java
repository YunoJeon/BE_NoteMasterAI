package com.be_notemasterai.group.dto;

import com.be_notemasterai.group.entity.Group;
import lombok.Builder;

@Builder
public record GroupResponse(

    Long groupId,
    String groupName,
    String groupDescription
) {

  public static GroupResponse fromEntity(Group group) {

    return GroupResponse.builder()
        .groupId(group.getId())
        .groupName(group.getGroupName())
        .groupDescription(group.getGroupDescription())
        .build();
  }
}
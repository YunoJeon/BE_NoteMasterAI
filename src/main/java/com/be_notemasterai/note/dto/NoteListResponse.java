package com.be_notemasterai.note.dto;

import com.be_notemasterai.note.entity.Note;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NoteListResponse(

    Long noteId,
    Long ownerId,
    String ownerProfileImageUrl,
    Long groupId,
    String ownerNickname,
    String title,
    LocalDateTime createdAt
) {

  public static NoteListResponse fromEntity(Note note) {

    return NoteListResponse.builder()
        .noteId(note.getId())
        .ownerId(note.getOwner().getId())
        .ownerProfileImageUrl(note.getOwner().getProfileImageUrl())
        .groupId(note.getGroup() != null ? note.getGroup().getId() : null)
        .ownerNickname(note.getOwner().getNickname())
        .title(note.getTitle())
        .createdAt(note.getCreatedAt())
        .build();
  }
}
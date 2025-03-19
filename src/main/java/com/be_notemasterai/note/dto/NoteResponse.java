package com.be_notemasterai.note.dto;

import com.be_notemasterai.note.entity.Note;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NoteResponse(

    Long noteId,
    String title,
    String originalText,
    String summary,
    LocalDateTime createdAt
) {

  public static NoteResponse fromEntity(Note note) {

    return NoteResponse.builder()
        .noteId(note.getId())
        .title(note.getTitle())
        .originalText(note.getOriginalText())
        .summary(note.getSummary())
        .createdAt(note.getCreatedAt())
        .build();
  }
}
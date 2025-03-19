package com.be_notemasterai.note.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.dto.NoteListResponse;
import com.be_notemasterai.note.dto.NoteResponse;
import com.be_notemasterai.note.dto.NoteUpdateRequest;
import com.be_notemasterai.note.service.NoteService;
import com.be_notemasterai.security.resolver.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class NoteController {

  private final NoteService noteService;

  @GetMapping
  public ResponseEntity<Page<NoteListResponse>> getNotes(
      @CurrentMember Member owner,
      @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {

    return ResponseEntity.ok(noteService.getNotes(owner, pageable));
  }

  @GetMapping("/{noteId}")
  public ResponseEntity<NoteResponse> getNote(@CurrentMember Member owner,
      @PathVariable Long noteId) {

    return ResponseEntity.ok(noteService.getNote(owner, noteId));
  }

  @PostMapping("/{noteId}")
  public ResponseEntity<NoteResponse> updateNote(@CurrentMember Member owner,
      @PathVariable Long noteId, @RequestBody NoteUpdateRequest noteUpdateRequest) {

    return ResponseEntity.ok(noteService.updateNote(owner, noteId, noteUpdateRequest));
  }

  @DeleteMapping("/{noteId}")
  public ResponseEntity<Void> deleteNote(@CurrentMember Member owner, @PathVariable Long noteId) {

    noteService.deleteNote(owner, noteId);

    return ResponseEntity.ok().build();
  }
}
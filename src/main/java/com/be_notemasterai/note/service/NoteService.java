package com.be_notemasterai.note.service;

import static com.be_notemasterai.exception.ErrorCode.NOTE_OWNER_MISMATCH;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_NOTE;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.dto.NoteListResponse;
import com.be_notemasterai.note.dto.NoteResponse;
import com.be_notemasterai.note.dto.NoteUpdateRequest;
import com.be_notemasterai.note.entity.Note;
import com.be_notemasterai.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {

  private final NoteRepository noteRepository;

  @Transactional
  public void createNote(Member owner, String title, String originalText, String summary) {

    noteRepository.save(Note.of(owner, title, originalText, summary));
  }

  public Page<NoteListResponse> getNotes(Member owner, Pageable pageable) {

    Page<Note> notes = noteRepository.findByOwner(owner, pageable);

    return notes.map(NoteListResponse::fromEntity);
  }

  public NoteResponse getNote(Member owner, Long noteId) {

    Note note = findNoteAndOwner(owner, noteId);

    return NoteResponse.fromEntity(note);
  }

  @Transactional
  public NoteResponse updateNote(Member owner, Long noteId, NoteUpdateRequest noteUpdateRequest) {

    Note note = findNoteAndOwner(owner, noteId);

    note.updateNote(noteUpdateRequest);

    return NoteResponse.fromEntity(note);
  }

  @Transactional
  public void deleteNote(Member owner, Long noteId) {

    Note note = findNoteAndOwner(owner, noteId);

    noteRepository.delete(note);
  }

  public Note findNoteAndOwner(Member owner, Long noteId) {

    Note note = noteRepository.findById(noteId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_NOTE));

    if (!owner.getId().equals(note.getOwner().getId())) {
      throw new CustomException(NOTE_OWNER_MISMATCH);
    }

    return note;
  }
}
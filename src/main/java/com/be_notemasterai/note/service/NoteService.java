package com.be_notemasterai.note.service;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.entity.Note;
import com.be_notemasterai.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
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
}
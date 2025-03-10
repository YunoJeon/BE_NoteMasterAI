package com.be_notemasterai.note.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.entity.Note;
import com.be_notemasterai.note.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

  @Mock
  private NoteRepository noteRepository;

  @InjectMocks
  private NoteService noteService;

  private Member member;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .provider("google")
        .providerUuid("providerUuid")
        .name("이름")
        .profileImageUrl("프로필이미지")
        .tag("태그")
        .build();
  }

  @Test
  @DisplayName("노트가 성공적으로 생성된다")
  void createNote_success() {
    // given
    // when
    noteService.createNote(member, "제목", "원본", "요약");
    // then
    verify(noteRepository).save(any(Note.class));
  }
}
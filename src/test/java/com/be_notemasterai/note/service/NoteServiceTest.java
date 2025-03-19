package com.be_notemasterai.note.service;

import static com.be_notemasterai.exception.ErrorCode.NOTE_OWNER_MISMATCH;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_NOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.dto.NoteListResponse;
import com.be_notemasterai.note.dto.NoteResponse;
import com.be_notemasterai.note.dto.NoteUpdateRequest;
import com.be_notemasterai.note.entity.Note;
import com.be_notemasterai.note.repository.NoteRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        .id(1L)
        .provider("google")
        .providerUuid("providerUuid")
        .name("이름")
        .profileImageUrl("프로필이미지")
        .tag("태그")
        .build();
  }

  @Test
  @DisplayName("노트가 성공적으로 생성된다")
  void create_note_success() {
    // given
    // when
    noteService.createNote(member, "제목", "원본", "요약");
    // then
    verify(noteRepository).save(any(Note.class));
  }

  @Test
  @DisplayName("노트 목록을 성공적으로 반환한다")
  void get_notes_success() {
    // given
    Pageable pageable = PageRequest.of(0, 10, DESC, "createdAt");
    List<Note> noteList = List.of(
        Note.of(member, "제목1", "원본1", "요약1"),
        Note.of(member, "제목2", "원본2", "요약2"));

    Page<Note> page = new PageImpl<>(noteList, pageable, noteList.size());

    when(noteRepository.findByOwner(member, pageable)).thenReturn(page);
    // when
    Page<NoteListResponse> result = noteService.getNotes(member, pageable);
    // then
    assertEquals(2, result.getTotalElements());
    assertEquals("제목2", result.getContent().get(1).title());
  }

  @Test
  @DisplayName("노트 상세를 성공적으로 반환한다")
  void get_note_success() {
    // given
    Note note = Note.of(member, "제목", "원본", "요약");
    when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
    // when
    NoteResponse result = noteService.getNote(member, 1L);
    // then
    assertNotNull(result);
    assertEquals("제목", result.title());
    assertEquals("요약", result.summary());
  }

  @Test
  @DisplayName("노트의 주인이 아니면 예외가 발생한다")
  void get_note_failure_owner_mismatch() {
    // given
    Member anotherMember = Member.builder().id(2L).providerUuid("anotherProviderUuid").build();
    Note note = Note.of(member, "제목", "원본", "요약");

    when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> noteService.getNote(anotherMember, 1L));
    // then
    assertEquals(e.getErrorCode(), NOTE_OWNER_MISMATCH);
  }

  @Test
  @DisplayName("노트가 존재하지 않으면 예외가 발생한다")
  void get_note_failure_not_found() {
    // given
    when(noteRepository.findById(1L)).thenReturn(Optional.empty());
    // when
    CustomException e = assertThrows(CustomException.class, () -> noteService.getNote(member, 1L));
    // then
    assertEquals(e.getErrorCode(), NOT_FOUND_NOTE);
  }

  @Test
  @DisplayName("노트 수정에 성공한다")
  void update_note_success() {
    // given
    Note note = Note.of(member, "제목", "원본", "요약");
    NoteUpdateRequest noteUpdateRequest = new NoteUpdateRequest("수정 제목", "수정 요약");

    when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
    // when
    NoteResponse result = noteService.updateNote(member, 1L, noteUpdateRequest);
    // then
    assertEquals("수정 제목", result.title());
    assertEquals("수정 요약", result.summary());
  }

  @Test
  @DisplayName("노트 삭제에 성공한다")
  void delete_note_success() {
    // given
    Note note = Note.of(member, "제목", "원본", "요약");

    when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
    // when
    noteService.deleteNote(member, 1L);
    // then
    verify(noteRepository).delete(note);
  }
}
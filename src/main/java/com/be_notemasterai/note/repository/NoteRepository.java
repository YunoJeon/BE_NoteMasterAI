package com.be_notemasterai.note.repository;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

  Page<Note> findByOwner(Member member, Pageable pageable);
}
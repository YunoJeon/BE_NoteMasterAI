package com.be_notemasterai.note.repository;

import com.be_notemasterai.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

}
package com.be_notemasterai.note.repository;

import com.be_notemasterai.group.entity.Group;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.entity.Note;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

  Page<Note> findByOwner(Member member, Pageable pageable);

  Page<Note> findByGroup(Group group, Pageable pageable);

  List<Note> findByGroup(Group group);

  List<Note> findByOwnerAndGroup(Member member, Group group);
}
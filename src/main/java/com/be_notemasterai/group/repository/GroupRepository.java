package com.be_notemasterai.group.repository;

import com.be_notemasterai.group.entity.Group;
import com.be_notemasterai.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group, Long> {

  @Query("SELECT g FROM Group g JOIN GroupMember gm ON g.id = gm.group.id WHERE gm.member = :member")
  Page<Group> findGroupsByMember(@Param("member") Member member, Pageable pageable);
}
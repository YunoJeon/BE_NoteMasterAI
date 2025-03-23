package com.be_notemasterai.group.repository;

import com.be_notemasterai.group.entity.Group;
import com.be_notemasterai.group.entity.GroupMember;
import com.be_notemasterai.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

  long countByGroup(Group group);

  Optional<GroupMember> findByMemberAndGroup(Member member, Group group);

  boolean existsByMemberAndGroup(Member member, Group group);

  Page<GroupMember> findByGroup(Group group, Pageable pageable);

  Long countByMember(Member member);

  List<GroupMember> findByMember(Member member);
}
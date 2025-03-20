package com.be_notemasterai.group.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

import com.be_notemasterai.group.type.GroupRole;
import com.be_notemasterai.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "group_member")
@Builder
public class GroupMember {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  @OnDelete(action = CASCADE)
  private Group group;

  @Enumerated(STRING)
  @Column(name = "group_role", nullable = false)
  private GroupRole groupRole;

  public static GroupMember of(Member member, Group group, GroupRole groupRole) {

    return GroupMember.builder()
        .member(member)
        .group(group)
        .groupRole(groupRole)
        .build();
  }
}
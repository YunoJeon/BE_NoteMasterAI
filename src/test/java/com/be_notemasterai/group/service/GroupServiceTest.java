package com.be_notemasterai.group.service;

import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_GROUP_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_GROUP_NOTE;
import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_GROUP_NULL;
import static com.be_notemasterai.exception.ErrorCode.CANNOT_LEAVE_GROUP_AS_OWNER;
import static com.be_notemasterai.exception.ErrorCode.EXIST_GROUP_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.INVALID_GROUP_NOTE;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_GROUP;
import static com.be_notemasterai.exception.ErrorCode.NOT_GROUP_OWNER;
import static com.be_notemasterai.exception.ErrorCode.SELF_INVITE_NOT_ALLOWED;
import static com.be_notemasterai.group.type.GroupRole.INVITEE;
import static com.be_notemasterai.group.type.GroupRole.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.group.dto.GroupListResponse;
import com.be_notemasterai.group.dto.GroupRequest;
import com.be_notemasterai.group.dto.GroupResponse;
import com.be_notemasterai.group.dto.GroupUpdateRequest;
import com.be_notemasterai.group.entity.Group;
import com.be_notemasterai.group.entity.GroupMember;
import com.be_notemasterai.group.repository.GroupMemberRepository;
import com.be_notemasterai.group.repository.GroupRepository;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.member.repository.MemberRepository;
import com.be_notemasterai.note.dto.NoteListResponse;
import com.be_notemasterai.note.entity.Note;
import com.be_notemasterai.note.repository.NoteRepository;
import com.be_notemasterai.note.service.NoteService;
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

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

  @Mock
  private GroupRepository groupRepository;

  @Mock
  private GroupMemberRepository groupMemberRepository;

  @Mock
  private NoteService noteService;

  @Mock
  private NoteRepository noteRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private GroupService groupService;

  private Member owner;
  private Member invitee;
  private Group group;
  private Note note;
  private GroupMember groupMember;

  @BeforeEach
  void setUp() {
    owner = Member.builder()
        .id(1L)
        .tag("@tag")
        .build();

    invitee = Member.builder()
        .id(2L)
        .tag("@tagTag")
        .build();

    group = Group.builder()
        .id(1L)
        .groupName("그룹명")
        .groupDescription("그룹 설명")
        .build();

    groupMember = GroupMember.builder()
        .id(1L)
        .member(owner)
        .groupRole(OWNER)
        .build();
  }

  @Test
  @DisplayName("그룹 생성에 성공한다")
  void create_group_success() {
    // given
    GroupRequest groupRequest = new GroupRequest("그룹", "설명");
    // when
    groupService.createGroup(owner, groupRequest);
    // then
    verify(groupRepository).save(any());
    verify(groupMemberRepository).save(any());
  }

  @Test
  @DisplayName("그룹에 노트를 성공적으로 추가한다")
  void add_note_group_success() {
    // given
    note = Note.builder().id(1L).owner(owner).build();

    when(noteService.findNoteAndOwner(owner, note.getId())).thenReturn(note);

    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(noteRepository.findByGroup(group, PageRequest.of(0, 10))).thenReturn(
        new PageImpl<>(List.of(note), PageRequest.of(0, 10), 1));
    // when
    Page<NoteListResponse> noteListResponses = groupService.addNoteToGroup(owner, group.getId(),
        note.getId(), PageRequest.of(0, 10));
    // then
    assertNotNull(noteListResponses);
    assertEquals(1, noteListResponses.getTotalElements());
  }

  @Test
  @DisplayName("노트가 그룹이 있으면 예외가 발생한다")
  void add_note_group_failure_already_set_note() {
    // given
    note = Note.builder().id(1L).group(group).build();

    when(noteService.findNoteAndOwner(owner, note.getId())).thenReturn(note);
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.addNoteToGroup(owner, group.getId(), note.getId(),
            PageRequest.of(0, 10)));
    // then
    assertEquals(ALREADY_SET_GROUP_NOTE, e.getErrorCode());
  }

  @Test
  @DisplayName("그룹이 존재하지 않으면 예외가 발생한다")
  void add_note_group_failure_not_found_group() {
    // given
    note = Note.builder().id(1L).build();

    when(noteService.findNoteAndOwner(owner, note.getId())).thenReturn(note);

    when(groupRepository.findById(group.getId())).thenReturn(Optional.empty());
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.addNoteToGroup(owner, group.getId(), note.getId(),
            PageRequest.of(0, 10)));
    // then
    assertEquals(NOT_FOUND_GROUP, e.getErrorCode());
  }

  @Test
  @DisplayName("그룹에서 노트를 성공적으로 제거한다")
  void remove_note_from_group_success() {
    // given
    note = Note.builder().id(1L).owner(owner).group(group).build();

    when(noteService.findNoteAndOwner(owner, note.getId())).thenReturn(note);

    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(noteRepository.findByGroup(group, PageRequest.of(0, 10))).thenReturn(
        new PageImpl<>(List.of(note), PageRequest.of(0, 10), 1));
    // when
    groupService.removeNoteFromGroup(owner, group.getId(), note.getId(), PageRequest.of(0, 10));
    // then
    assertNull(note.getGroup());
  }

  @Test
  @DisplayName("이미 노트가 제거되었으면 예외가 발생한다")
  void remove_note_from_group_failure_already_remove() {
    // given
    note = Note.builder().id(1L).owner(owner).build();

    when(noteService.findNoteAndOwner(owner, note.getId())).thenReturn(note);

    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.removeNoteFromGroup(owner, group.getId(), note.getId(),
            PageRequest.of(0, 10)));
    // then
    assertEquals(e.getErrorCode(), ALREADY_SET_GROUP_NULL);
  }

  @Test
  @DisplayName("그룹에 속한 노트가 아니면 예외가 발생한다")
  void remove_note_from_group_failure_invalid_group() {
    // given
    Group anotherGroup = Group.builder().id(2L).build();
    note = Note.builder().id(1L).owner(owner).group(anotherGroup).build();

    when(noteService.findNoteAndOwner(owner, note.getId())).thenReturn(note);

    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.removeNoteFromGroup(owner, group.getId(), note.getId(),
            PageRequest.of(0, 10)));
    // then
    assertEquals(INVALID_GROUP_NOTE, e.getErrorCode());
  }

  @Test
  @DisplayName("그룹 목록 조회에 성공한다")
  void get_groups_success() {
    // given
    Group anotherGroup = Group.builder().id(2L).build();

    when(groupRepository.findGroupsByMember(owner, PageRequest.of(0, 10))).thenReturn(
        new PageImpl<>(List.of(group, anotherGroup), PageRequest.of(0, 10), 2));
    // when
    Page<GroupListResponse> groups = groupService.getGroups(owner, PageRequest.of(0, 10));
    // then
    assertEquals(2, groups.getTotalElements());
  }

  @Test
  @DisplayName("그룹 상세 조회에 성공한다")
  void get_group_success() {
    // given
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(owner, group)).thenReturn(
        Optional.of(groupMember));
    // when
    GroupResponse result = groupService.getGroup(owner, group.getId());
    // then
    assertEquals(result.groupName(), "그룹명");
    assertEquals(result.groupDescription(), "그룹 설명");
  }

  @Test
  @DisplayName("그룹 삭제에 성공한다")
  void delete_group_success() {
    // given
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(owner, group)).thenReturn(
        Optional.of(groupMember));
    // when
    groupService.deleteGroup(owner, group.getId());
    // then
    verify(groupRepository).delete(group);
  }

  @Test
  @DisplayName("그룹장이 아니면 예외가 발생한다")
  void delete_group_failure_not_owner() {
    // given
    GroupMember inviteMember = GroupMember.builder().id(2L).group(group).groupRole(INVITEE).build();

    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(invitee, group)).thenReturn(
        Optional.of(inviteMember));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.deleteGroup(invitee, group.getId()));
    // then
    assertEquals(e.getErrorCode(), NOT_GROUP_OWNER);
  }

  @Test
  @DisplayName("그룹에 다른 멤버가 남아있으면 예외가 발생한다")
  void delete_group_failure_exist_member() {
    // given
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(owner, group)).thenReturn(
        Optional.of(groupMember));

    when(groupMemberRepository.countByGroup(group)).thenReturn(2L);
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.deleteGroup(owner, group.getId()));
    // then
    assertEquals(e.getErrorCode(), EXIST_GROUP_MEMBER);
  }

  @Test
  @DisplayName("그룹 수정이 성공한다")
  void update_group_success() {
    // given
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(owner, group)).thenReturn(
        Optional.of(groupMember));
    // when
    GroupResponse groupResponse = groupService.updateGroup(owner, group.getId(),
        new GroupUpdateRequest("새 이름", "새 설명"));
    // then
    assertEquals(groupResponse.groupName(), "새 이름");
    assertEquals(groupResponse.groupDescription(), "새 설명");
  }

  @Test
  @DisplayName("그룹에 다른 회원 초대에 성공한다")
  void invite_member_success() {
    // given
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(owner, group)).thenReturn(
        Optional.of(groupMember));

    when(memberRepository.findById(invitee.getId())).thenReturn(Optional.of(invitee));
    // when
    groupService.inviteMember(owner, group.getId(), invitee.getId());
    // then
    verify(groupMemberRepository).save(any());
  }

  @Test
  @DisplayName("자기 자신을 초대하는 경우 예외가 발생한다")
  void invite_member_failure_self_invite() {
    // given
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(owner, group)).thenReturn(
        Optional.of(groupMember));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.inviteMember(owner, group.getId(), owner.getId()));
    // then
    assertEquals(e.getErrorCode(), SELF_INVITE_NOT_ALLOWED);
  }

  @Test
  @DisplayName("이미 해당 그룹에 속한 경우 예외가 발생한다")
  void invite_member_failure_already_member() {
    // given
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(owner, group)).thenReturn(
        Optional.of(groupMember));

    when(memberRepository.findById(invitee.getId())).thenReturn(Optional.of(invitee));

    when(groupMemberRepository.existsByMemberAndGroup(invitee, group)).thenReturn(true);
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.inviteMember(owner, group.getId(), invitee.getId()));
    // then
    assertEquals(e.getErrorCode(), ALREADY_SET_GROUP_MEMBER);
  }

  @Test
  @DisplayName("그룹에서 성공적으로 탈퇴한다")
  void leave_group_success() {
    // given
    GroupMember anotherMember = GroupMember.builder()
        .id(2L)
        .group(group)
        .member(invitee)
        .groupRole(INVITEE)
        .build();

    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(invitee, group)).thenReturn(
        Optional.of(anotherMember));
    // when
    groupService.leaveGroup(invitee, group.getId());
    // then
    verify(groupMemberRepository).delete(anotherMember);
  }

  @Test
  @DisplayName("그룹장인경우 그룹 탈퇴시 예외가 발생한다")
  void leave_group_failure_group_owner() {
    // given
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    when(groupMemberRepository.findByMemberAndGroup(owner, group)).thenReturn(
        Optional.of(groupMember));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> groupService.leaveGroup(owner, group.getId()));
    // then
    assertEquals(e.getErrorCode(), CANNOT_LEAVE_GROUP_AS_OWNER);
  }
}
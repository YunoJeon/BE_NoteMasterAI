package com.be_notemasterai.group.service;

import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_GROUP_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_GROUP_NOTE;
import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_GROUP_NULL;
import static com.be_notemasterai.exception.ErrorCode.CANNOT_LEAVE_GROUP_AS_OWNER;
import static com.be_notemasterai.exception.ErrorCode.CANNOT_REMOVE_GROUP_OWNER;
import static com.be_notemasterai.exception.ErrorCode.EXIST_GROUP_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.INVALID_GROUP_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.INVALID_GROUP_NOTE;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_GROUP;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.NOT_GROUP_OWNER;
import static com.be_notemasterai.exception.ErrorCode.SELF_INVITE_NOT_ALLOWED;
import static com.be_notemasterai.group.type.GroupRole.INVITEE;
import static com.be_notemasterai.group.type.GroupRole.OWNER;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.group.dto.GroupListResponse;
import com.be_notemasterai.group.dto.GroupMemberListResponse;
import com.be_notemasterai.group.dto.GroupRequest;
import com.be_notemasterai.group.dto.GroupResponse;
import com.be_notemasterai.group.dto.GroupUpdateRequest;
import com.be_notemasterai.group.entity.Group;
import com.be_notemasterai.group.entity.GroupMember;
import com.be_notemasterai.group.repository.GroupMemberRepository;
import com.be_notemasterai.group.repository.GroupRepository;
import com.be_notemasterai.group.type.GroupRole;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.member.repository.MemberRepository;
import com.be_notemasterai.note.dto.NoteListResponse;
import com.be_notemasterai.note.entity.Note;
import com.be_notemasterai.note.repository.NoteRepository;
import com.be_notemasterai.note.service.NoteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;

  private final GroupMemberRepository groupMemberRepository;

  private final NoteService noteService;

  private final NoteRepository noteRepository;

  private final MemberRepository memberRepository;

  @Transactional
  public GroupResponse createGroup(Member member, GroupRequest groupRequest) {

    Group group = Group.of(groupRequest);

    groupRepository.save(group);

    saveGroupMember(member, group, OWNER);

    return GroupResponse.fromEntity(group);
  }

  @Transactional
  public void saveGroupMember(Member member, Group group, GroupRole groupRole) {

    GroupMember groupMember = GroupMember.of(member, group, groupRole);

    groupMemberRepository.save(groupMember);
  }

  @Transactional
  public Page<NoteListResponse> addNoteToGroup(Member member, Long groupId, Long noteId,
      Pageable pageable) {

    Note note = noteService.findNoteAndOwner(member, noteId);

    if (note.getGroup() != null) {
      throw new CustomException(ALREADY_SET_GROUP_NOTE);
    }

    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

    note.setGroup(group);

    return notePage(group, pageable);
  }

  @Transactional
  public Page<NoteListResponse> removeNoteFromGroup(Member member, Long groupId, Long noteId,
      Pageable pageable) {

    Note note = noteService.findNoteAndOwner(member, noteId);

    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

    if (note.getGroup() == null) {
      throw new CustomException(ALREADY_SET_GROUP_NULL);
    }

    if (!note.getGroup().getId().equals(groupId)) {
      throw new CustomException(INVALID_GROUP_NOTE);
    }

    note.setGroup(null);

    return notePage(group, pageable);
  }

  public Page<GroupListResponse> getGroups(Member member, Pageable pageable) {

    Page<Group> groups = groupRepository.findGroupsByMember(member, pageable);

    return groups.map(
        group -> GroupListResponse.fromEntity(group, groupMemberRepository.countByGroup(group)));
  }

  public GroupResponse getGroup(Member member, Long groupId) {

    Group group = validGroupMember(member, groupId).getFirst();

    return GroupResponse.fromEntity(group);
  }

  @Transactional
  public void deleteGroup(Member member, Long groupId) {

    Pair<Group, GroupMember> pair = validGroupMember(member, groupId);

    if (pair.getSecond().getGroupRole() != OWNER) {
      throw new CustomException(NOT_GROUP_OWNER);
    }

    long memberCount = groupMemberRepository.countByGroup(pair.getFirst());

    if (memberCount > 1) {
      throw new CustomException(EXIST_GROUP_MEMBER);
    }

    List<Note> notes = noteRepository.findByGroup(pair.getFirst());
    noteSetGroupNull(notes);

    groupRepository.delete(pair.getFirst());
  }

  @Transactional
  public GroupResponse updateGroup(Member member, Long groupId,
      GroupUpdateRequest groupUpdateRequest) {

    Pair<Group, GroupMember> pair = validGroupMember(member, groupId);

    if (pair.getSecond().getGroupRole() != OWNER) {
      throw new CustomException(NOT_GROUP_OWNER);
    }

    pair.getFirst().update(groupUpdateRequest);

    return GroupResponse.fromEntity(pair.getFirst());
  }

  @Transactional
  public void inviteMember(Member owner, Long groupId, Long memberId) {

    Pair<Group, GroupMember> pair = validGroupMember(owner, groupId);

    if (owner.getId().equals(memberId)) {
      throw new CustomException(SELF_INVITE_NOT_ALLOWED);
    }

    if (pair.getSecond().getGroupRole() != OWNER) {
      throw new CustomException(NOT_GROUP_OWNER);
    }

    Member inviteMember = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

    boolean existMember = groupMemberRepository.existsByMemberAndGroup(inviteMember,
        pair.getFirst());

    if (existMember) {
      throw new CustomException(ALREADY_SET_GROUP_MEMBER);
    }

    saveGroupMember(inviteMember, pair.getFirst(), INVITEE);
  }

  @Transactional
  public void leaveGroup(Member member, Long groupId) {

    Pair<Group, GroupMember> pair = validGroupMember(member, groupId);

    if (pair.getSecond().getGroupRole() == OWNER) {
      throw new CustomException(CANNOT_LEAVE_GROUP_AS_OWNER);
    }

    removeGroupMemberAndNotes(pair.getSecond(), member, pair.getFirst());
  }

  public Page<NoteListResponse> getNotesInGroup(Member member, Long groupId, Pageable pageable) {

    Pair<Group, GroupMember> pair = validGroupMember(member, groupId);

    return notePage(pair.getFirst(), pageable);
  }

  @Transactional
  public void removeMemberFromGroup(Member member, Long groupId, Long memberId) {

    if (member.getId().equals(memberId)) {
      throw new CustomException(CANNOT_REMOVE_GROUP_OWNER);
    }

    Pair<Group, GroupMember> pair = validGroupMember(member, groupId);

    if (pair.getSecond().getGroupRole() != OWNER) {
      throw new CustomException(NOT_GROUP_OWNER);
    }

    Member targetMember = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

    GroupMember groupMember = groupMemberRepository.findByMemberAndGroup(targetMember,
        pair.getFirst()).orElseThrow(() -> new CustomException(INVALID_GROUP_MEMBER));

    removeGroupMemberAndNotes(groupMember, targetMember, pair.getFirst());
  }


  public Page<GroupMemberListResponse> getGroupMembers(Member member, Long groupId, Pageable pageable) {

    Pair<Group, GroupMember> pair = validGroupMember(member, groupId);

    Page<GroupMember> groupMembers = groupMemberRepository.findByGroup(pair.getFirst(), pageable);

    return groupMembers.map(GroupMemberListResponse::fromEntity);
  }

  private void removeGroupMemberAndNotes(GroupMember groupMember, Member member, Group group) {

    groupMemberRepository.delete(groupMember);

    List<Note> notes = noteRepository.findByOwnerAndGroup(member, group);
    noteSetGroupNull(notes);
  }

  private Page<NoteListResponse> notePage(Group group, Pageable pageable) {

    Page<Note> notes = noteRepository.findByGroup(group, pageable);

    return notes.map(NoteListResponse::fromEntity);
  }

  private Pair<Group, GroupMember> validGroupMember(Member member, Long groupId) {

    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

    GroupMember groupMember = groupMemberRepository.findByMemberAndGroup(member, group)
        .orElseThrow(() -> new CustomException(INVALID_GROUP_MEMBER));

    return Pair.of(group, groupMember);
  }

  private void noteSetGroupNull(List<Note> notes) {
    notes.forEach(note -> note.setGroup(null));
  }
}
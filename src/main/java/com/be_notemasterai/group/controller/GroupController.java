package com.be_notemasterai.group.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.be_notemasterai.group.dto.GroupListResponse;
import com.be_notemasterai.group.dto.GroupMemberListResponse;
import com.be_notemasterai.group.dto.GroupRequest;
import com.be_notemasterai.group.dto.GroupResponse;
import com.be_notemasterai.group.dto.GroupUpdateRequest;
import com.be_notemasterai.group.service.GroupService;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.dto.NoteListResponse;
import com.be_notemasterai.security.resolver.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

  private final GroupService groupService;

  @PostMapping
  public ResponseEntity<GroupResponse> createGroup(@CurrentMember Member member,
      @RequestBody GroupRequest groupRequest) {

    return ResponseEntity.ok(groupService.createGroup(member, groupRequest));
  }

  @PutMapping("/{groupId}/notes/{noteId}")
  public ResponseEntity<Page<NoteListResponse>> addNoteToGroup(@CurrentMember Member member,
      @PathVariable Long groupId,
      @PathVariable Long noteId,
      @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {

    return ResponseEntity.ok(groupService.addNoteToGroup(member, groupId, noteId, pageable));
  }

  @DeleteMapping("/{groupId}/notes/{noteId}")
  public ResponseEntity<Page<NoteListResponse>> removeNoteFromGroup(@CurrentMember Member member,
      @PathVariable Long groupId, @PathVariable Long noteId,
      @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {

    return ResponseEntity.ok(groupService.removeNoteFromGroup(member, groupId, noteId, pageable));
  }

  @GetMapping
  public ResponseEntity<Page<GroupListResponse>> getGroups(@CurrentMember Member member,
      @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {

    return ResponseEntity.ok(groupService.getGroups(member, pageable));
  }

  @GetMapping("/{groupId}")
  public ResponseEntity<GroupResponse> getGroup(@CurrentMember Member member,
      @PathVariable Long groupId) {

    return ResponseEntity.ok(groupService.getGroup(member, groupId));
  }

  @DeleteMapping("/{groupId}")
  public ResponseEntity<Void> deleteGroup(@CurrentMember Member member,
      @PathVariable Long groupId) {

    groupService.deleteGroup(member, groupId);

    return ResponseEntity.ok().build();
  }

  @PutMapping("/{groupId}")
  public ResponseEntity<GroupResponse> updateGroup(@CurrentMember Member member,
      @PathVariable Long groupId,
      @RequestBody GroupUpdateRequest groupUpdateRequest) {

    return ResponseEntity.ok(groupService.updateGroup(member, groupId, groupUpdateRequest));
  }

  @PostMapping("/{groupId}/members/{memberId}")
  public ResponseEntity<Void> inviteMember(@CurrentMember Member owner, @PathVariable Long groupId,
      @PathVariable Long memberId) {

    groupService.inviteMember(owner, groupId, memberId);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{groupId}/leave")
  public ResponseEntity<Void> leaveGroup(@CurrentMember Member member, @PathVariable Long groupId) {

    groupService.leaveGroup(member, groupId);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{groupId}/notes")
  public ResponseEntity<Page<NoteListResponse>> getNotesInGroup(@CurrentMember Member member,
      @PathVariable Long groupId,
      @PageableDefault(sort = "cratedAt", direction = DESC) Pageable pageable) {

    return ResponseEntity.ok(groupService.getNotesInGroup(member, groupId, pageable));
  }

  @DeleteMapping("/{groupId}/members/{memberId}")
  public ResponseEntity<Void> removeMemberFromGroup(@CurrentMember Member member,
      @PathVariable Long groupId, @PathVariable Long memberId) {

    groupService.removeMemberFromGroup(member, groupId, memberId);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{groupId}/members")
  public ResponseEntity<Page<GroupMemberListResponse>> getGroupMembers(@CurrentMember Member member,
      @PathVariable Long groupId,
      @PageableDefault(sort = "groupRole", direction = DESC) Pageable pageable) {

    return ResponseEntity.ok(groupService.getGroupMembers(member, groupId, pageable));
  }
}
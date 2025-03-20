package com.be_notemasterai.member.controller;

import com.be_notemasterai.member.dto.MemberInfoResponse;
import com.be_notemasterai.member.dto.NicknameRequestDto;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.member.service.MemberService;
import com.be_notemasterai.security.resolver.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

  private final MemberService memberService;

  @PutMapping("/nickname")
  public ResponseEntity<Void> setupNickname(@CurrentMember Member member,
      @RequestBody NicknameRequestDto nicknameRequestDto) {

    memberService.setupNickname(member, nicknameRequestDto.nickname());

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{tag}")
  public ResponseEntity<MemberInfoResponse> getMember(@CurrentMember Member member,
      @PathVariable String tag) {

    return ResponseEntity.ok(memberService.getMember(member, tag));
  }
}
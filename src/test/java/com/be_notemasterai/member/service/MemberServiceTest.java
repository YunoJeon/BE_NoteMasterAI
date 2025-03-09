package com.be_notemasterai.member.service;

import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_NICKNAME;
import static com.be_notemasterai.exception.ErrorCode.EXISTS_NICKNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private MemberService memberService;

  private Member member;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .provider("google")
        .name("이름")
        .profileImageUrl("프로필이미지")
        .tag("tag")
        .build();
  }

  @Test
  @DisplayName("닉네임 설정이 정상적으로 이루어진다")
  void setUpNickname_success() {
    // given
    String nickname = "닉네임";

    when(memberRepository.existsByNickname(nickname)).thenReturn(false);
    // when
    memberService.setupNickname(member, nickname);
    // then
    assertEquals(nickname, member.getNickname());
  }

  @Test
  @DisplayName("닉네임이 이미 설정된 경우 예외가 발생한다")
  void setUpNickname_failure_AlreadySet() {
    // given
    member.setNickname("닉네임");
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> memberService.setupNickname(member, "새 닉네임"));
    // then
    assertEquals(e.getMessage(), "이미 닉네임이 설정되었습니다.");
    assertEquals(e.getErrorCode(), ALREADY_SET_NICKNAME);
  }

  @Test
  @DisplayName("닉네임이 중복된 경우 예외가 발생한다")
  void setUpNickname_failure_DuplicateNickname() {
    // given
    String nickname = "닉네임";

    when(memberRepository.existsByNickname(nickname)).thenReturn(true);
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> memberService.setupNickname(member, nickname));
    // then
    assertEquals(e.getMessage(), "이미 존재하는 닉네임 입니다.");
    assertEquals(e.getErrorCode(), EXISTS_NICKNAME);
  }
}
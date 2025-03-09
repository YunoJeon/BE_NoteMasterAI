package com.be_notemasterai.member.service;

import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_NICKNAME;
import static com.be_notemasterai.exception.ErrorCode.EXISTS_NICKNAME;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional
  public void setupNickname(Member member, String nickname) {

    if (member.getNickname() != null) {
      throw new CustomException(ALREADY_SET_NICKNAME);
    }

    boolean existsNickname = memberRepository.existsByNickname(nickname);

    if (existsNickname) {
      throw new CustomException(EXISTS_NICKNAME);
    }

    member.setNickname(nickname);
  }
}
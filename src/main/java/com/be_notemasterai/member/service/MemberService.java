package com.be_notemasterai.member.service;

import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_NICKNAME;
import static com.be_notemasterai.exception.ErrorCode.EXISTS_NICKNAME;
import static com.be_notemasterai.exception.ErrorCode.EXISTS_GROUP_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.INVALID_TAG;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_PAYMENT;
import static com.be_notemasterai.exception.ErrorCode.SELF_TAG_SEARCH_NOT_ALLOWED;
import static com.be_notemasterai.group.type.GroupRole.OWNER;
import static com.be_notemasterai.subscribe.type.SubscriptionStatus.ACTIVE;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.group.entity.GroupMember;
import com.be_notemasterai.group.repository.GroupMemberRepository;
import com.be_notemasterai.group.repository.GroupRepository;
import com.be_notemasterai.group.service.GroupService;
import com.be_notemasterai.member.dto.MemberInfoResponse;
import com.be_notemasterai.member.dto.MemberUpdateRequest;
import com.be_notemasterai.member.dto.MyInfoResponse;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.member.repository.MemberRepository;
import com.be_notemasterai.note.repository.NoteRepository;
import com.be_notemasterai.payment.entity.Payment;
import com.be_notemasterai.payment.repository.PaymentRepository;
import com.be_notemasterai.payment.service.PaymentService;
import com.be_notemasterai.subscribe.entity.Subscribe;
import com.be_notemasterai.subscribe.repository.SubscribeRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;

  private final NoteRepository noteRepository;

  private final GroupMemberRepository groupMemberRepository;

  private final SubscribeRepository subscribeRepository;

  private final PaymentService paymentService;

  private final PaymentRepository paymentRepository;

  private final GroupRepository groupRepository;

  private final GroupService groupService;

  @Transactional
  public void setupNickname(Member member, String nickname) {

    if (member.getNickname() != null) {
      throw new CustomException(ALREADY_SET_NICKNAME);
    }

    checkAndSetNickname(member, nickname);
  }

  public MemberInfoResponse getMember(Member member, String tag) {

    Member getMemberByTag = memberRepository.findByTag(tag)
        .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

    if (member.getId().equals(getMemberByTag.getId())) {
      throw new CustomException(SELF_TAG_SEARCH_NOT_ALLOWED);
    }

    return MemberInfoResponse.fromEntity(getMemberByTag);
  }

  public MyInfoResponse getMyInfo(Member member) {

    Long countNote = noteRepository.countByOwner(member);

    Long countGroups = groupMemberRepository.countByMember(member);

    LocalDateTime subscribeEndedAt =
        subscribeRepository.findActiveSubscribeEndedAtBySubscriber(member);

    return MyInfoResponse.fromEntity(member, countNote, countGroups, subscribeEndedAt);
  }

  @Transactional
  public MyInfoResponse updateInfo(Member member, MemberUpdateRequest memberUpdateRequest) {

    checkAndSetNickname(member, memberUpdateRequest.nickname());

    return getMyInfo(member);
  }

  @Transactional
  public BigDecimal withdraw(Member member) {

    Subscribe activeSubscribe = subscribeRepository.findBySubscriberAndSubscriptionStatus(member,
        ACTIVE).orElse(null);

    if (activeSubscribe != null) {

      Payment payment = paymentRepository.findById(activeSubscribe.getPaymentId())
          .orElseThrow(() -> new CustomException(NOT_FOUND_PAYMENT));

      long daysUsed = Duration.between(activeSubscribe.getStartedAt(), LocalDateTime.now())
          .toDays();

      return paymentService.calculateRefundAmount(payment.getAmount(), daysUsed);
    }

    processMemberWithdrawal(member);

    return null;
  }

  @Transactional
  public void rejoin(String tag) {

    if (tag == null || tag.isEmpty()) {
      throw new CustomException(INVALID_TAG);
    }

    Member member = memberRepository.findByTag(tag)
        .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

    member.setDeletedAt(null);
  }

  private void processMemberWithdrawal(Member member) {

    List<GroupMember> groupMembers = groupMemberRepository.findByMember(member);

    for (GroupMember groupMember : groupMembers) {

      if (groupMember.getGroupRole() == OWNER) {

        long memberCount = groupMemberRepository.countByGroup(groupMember.getGroup());

        if (memberCount == 1) {
          groupRepository.delete(groupMember.getGroup());
        } else {
          throw new CustomException(EXISTS_GROUP_MEMBER);
        }
      } else {
        groupService.removeGroupMemberAndNotes(groupMember, member, groupMember.getGroup());
      }
    }

    member.setDeletedAt(LocalDateTime.now());
    member.setNickname(generateRandomNickname());
  }

  private void checkAndSetNickname(Member member, String nickname) {

    boolean existsNickname = memberRepository.existsByNickname(nickname);

    if (existsNickname) {
      throw new CustomException(EXISTS_NICKNAME);
    }

    member.setNickname(nickname);
  }

  private String generateRandomNickname() {
    return "user_" + System.currentTimeMillis();
  }
}
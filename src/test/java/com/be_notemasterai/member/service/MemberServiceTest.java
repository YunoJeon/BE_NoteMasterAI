package com.be_notemasterai.member.service;

import static com.be_notemasterai.exception.ErrorCode.ALREADY_SET_NICKNAME;
import static com.be_notemasterai.exception.ErrorCode.EXISTS_GROUP_MEMBER;
import static com.be_notemasterai.exception.ErrorCode.EXISTS_NICKNAME;
import static com.be_notemasterai.exception.ErrorCode.SELF_TAG_SEARCH_NOT_ALLOWED;
import static com.be_notemasterai.group.type.GroupRole.OWNER;
import static com.be_notemasterai.subscribe.type.SubscriptionStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.group.entity.Group;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

  @Mock
  private NoteRepository noteRepository;

  @Mock
  private GroupMemberRepository groupMemberRepository;

  @Mock
  private SubscribeRepository subscribeRepository;

  @Mock
  private PaymentService paymentService;

  @Mock
  private PaymentRepository paymentRepository;

  @Mock
  private GroupRepository groupRepository;

  @Mock
  private GroupService groupService;

  @InjectMocks
  private MemberService memberService;

  private Member member;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
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

  @Test
  @DisplayName("회원 조회에 성공한다")
  void get_member_success() {
    // given
    Member anotherMember = Member.builder().id(2L).nickname("닉").tag("@tagTag").build();
    when(memberRepository.findByTag(anotherMember.getTag())).thenReturn(Optional.of(anotherMember));
    // when
    MemberInfoResponse result = memberService.getMember(member, anotherMember.getTag());
    // then
    assertEquals(result.nickname(), anotherMember.getNickname());
  }

  @Test
  @DisplayName("자기 자신을 조회하면 예외가 발생한다")
  void get_member_failure_self_search() {
    // given
    when(memberRepository.findByTag(member.getTag())).thenReturn(Optional.of(member));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> memberService.getMember(member, member.getTag()));
    // then
    assertEquals(e.getErrorCode(), SELF_TAG_SEARCH_NOT_ALLOWED);
  }

  @Test
  @DisplayName("내 정보 조회가 성공한다")
  void get_my_info_success() {
    // given
    when(noteRepository.countByOwner(member)).thenReturn(0L);

    when(groupMemberRepository.countByMember(member)).thenReturn(0L);

    when(subscribeRepository.findActiveSubscribeEndedAtBySubscriber(member)).thenReturn(null);
    // when
    MyInfoResponse myInfo = memberService.getMyInfo(member);
    // then
    assertEquals(myInfo.nickname(), member.getNickname());
    assertNull(myInfo.subscribeEndedAt());
  }

  @Test
  @DisplayName("닉네임 수정이 성공한다")
  void update_info_success() {
    // given
    when(memberRepository.existsByNickname("변경")).thenReturn(false);
    // when
    memberService.updateInfo(member, new MemberUpdateRequest("변경"));
    // then
    assertEquals(member.getNickname(), "변경");
  }

  @Test
  @DisplayName("회원 탈퇴가 성공한다")
  void withdraw_success() {
    // given
    when(subscribeRepository.findBySubscriberAndSubscriptionStatus(member, ACTIVE))
        .thenReturn(Optional.empty());
    // when
    memberService.withdraw(member);
    // then
    assertNotNull(member.getDeletedAt());
  }

  @Test
  @DisplayName("구독이 활성화 되어있는 경우 환불 예상 금액이 반환된다")
  void withdraw_failure_subscription_active() {
    // given
    Subscribe subscribe = Subscribe.builder()
        .id(1L)
        .subscriber(member)
        .startedAt(LocalDateTime.now().minusDays(10))
        .endedAt(LocalDateTime.now().plusDays(20))
        .paymentId(1L)
        .build();

    Payment payment = Payment.builder()
        .id(1L)
        .member(member)
        .amount(BigDecimal.valueOf(9900.00))
        .build();
    when(subscribeRepository.findBySubscriberAndSubscriptionStatus(member, ACTIVE)).thenReturn(
        Optional.of(subscribe));

    when(paymentRepository.findById(subscribe.getPaymentId())).thenReturn(Optional.of(payment));
    // when
    BigDecimal refundAmount = memberService.withdraw(member);
    // then
    assertNull(member.getDeletedAt());
    BigDecimal expectedRefund = paymentService.calculateRefundAmount(payment.getAmount(), 10);
    assertEquals(refundAmount, expectedRefund);
  }

  @Test
  @DisplayName("그룹에 회원이 남아있는 경우 예외가 발생한다")
  void withdraw_failure_exists_group_member() {
    // given
    Group group = Group.builder()
        .id(1L)
        .build();

    GroupMember groupMember = GroupMember.builder()
        .id(1L)
        .member(member)
        .group(group)
        .groupRole(OWNER)
        .build();

    List<GroupMember> groupMembers = List.of(groupMember);

    when(subscribeRepository.findBySubscriberAndSubscriptionStatus(member, ACTIVE))
        .thenReturn(Optional.empty());

    when(groupMemberRepository.findByMember(member)).thenReturn(groupMembers);

    when(groupMemberRepository.countByGroup(group)).thenReturn(2L);
    // when
    CustomException e = assertThrows(CustomException.class, () -> memberService.withdraw(member));
    // then
    assertEquals(e.getErrorCode(), EXISTS_GROUP_MEMBER);
    assertNull(member.getDeletedAt());
  }
}
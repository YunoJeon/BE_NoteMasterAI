package com.be_notemasterai.subscribe.service;

import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_SUBSCRIPTION;
import static com.be_notemasterai.subscribe.type.SubscriptionStatus.ACTIVE;
import static com.be_notemasterai.subscribe.type.SubscriptionType.MONTH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.payment.entity.Payment;
import com.be_notemasterai.subscribe.entity.Subscribe;
import com.be_notemasterai.subscribe.repository.SubscribeRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

@ExtendWith(MockitoExtension.class)
class SubscribeServiceTest {

  @Mock
  private SubscribeRepository subscribeRepository;

  @Mock
  private TaskScheduler taskScheduler;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private SubscribeService subscribeService;

  @Test
  @DisplayName("구독 생성에 성공한다")
  void create_subscription_success() {
    // given
    Member member = mock(Member.class);
    Payment payment = mock(Payment.class);
    // when
    subscribeService.createSubscription(member, payment, MONTH);
    // then
    verify(subscribeRepository).save(any());
  }

  @Test
  @DisplayName("구독 취소에 성공한다")
  void cancel_subscription_success() {
    // given
    Payment payment = Payment.builder().id(1L).build();
    Subscribe subscribe = Subscribe.builder().paymentId(payment.getId()).subscriptionStatus(ACTIVE)
        .build();

    when(subscribeRepository.findByPaymentId(subscribe.getPaymentId())).thenReturn(
        Optional.of(subscribe));
    // when
    // then
    assertDoesNotThrow(() -> subscribeService.cancelSubscription(payment));
  }

  @Test
  @DisplayName("구독 내역이 없으면 취소에 실패한다")
  void cancel_subscription_failure_not_found_subscription() {
    // given
    Payment payment = Payment.builder().id(1L).build();

    when(subscribeRepository.findByPaymentId(anyLong())).thenReturn(Optional.empty());
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> subscribeService.cancelSubscription(payment));
    // then
    assertEquals(e.getErrorCode(), NOT_FOUND_SUBSCRIPTION);
  }
}
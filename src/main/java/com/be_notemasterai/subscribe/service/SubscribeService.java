package com.be_notemasterai.subscribe.service;

import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_SUBSCRIPTION;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.payment.entity.Payment;
import com.be_notemasterai.subscribe.entity.Subscribe;
import com.be_notemasterai.subscribe.event.SubscriptionExpiredEvent;
import com.be_notemasterai.subscribe.repository.SubscribeRepository;
import com.be_notemasterai.subscribe.type.SubscriptionType;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscribeService {

  private final SubscribeRepository subscribeRepository;

  private final TaskScheduler taskScheduler;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void createSubscription(Member member, Payment payment,
      SubscriptionType subscriptionType) {

    Subscribe subscribe = Subscribe.of(member, payment, subscriptionType);
    subscribeRepository.save(subscribe);

    scheduleSubscriptionExpiration(subscribe.getId(), subscribe.getEndedAt());
  }

  private void scheduleSubscriptionExpiration(Long id, LocalDateTime endedAt) {

    Duration delay = Duration.between(LocalDateTime.now(), endedAt);
    taskScheduler.schedule(() -> eventPublisher.publishEvent(new SubscriptionExpiredEvent(id)),
        Instant.now().plus(delay));
  }

  @Transactional
  public void cancelSubscription(Payment payment) {

    Subscribe subscribe = subscribeRepository.findByPaymentId(payment.getId())
        .orElseThrow(() -> new CustomException(NOT_FOUND_SUBSCRIPTION));

    subscribe.cancel();
  }
}
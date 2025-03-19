package com.be_notemasterai.subscribe.event.listener;

import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_SUBSCRIPTION;
import static com.be_notemasterai.subscribe.type.SubscriptionStatus.ACTIVE;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.subscribe.entity.Subscribe;
import com.be_notemasterai.subscribe.event.SubscriptionExpiredEvent;
import com.be_notemasterai.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SubscriptionEventListener {

  private final SubscribeRepository subscribeRepository;

  @EventListener
  @Transactional
  public void handleSubscriptionExpired(SubscriptionExpiredEvent event) {

    Subscribe subscribe = subscribeRepository.findById(event.subscribeId()).orElseThrow(() -> new CustomException(
        NOT_FOUND_SUBSCRIPTION));

    if (subscribe.getSubscriptionStatus() == ACTIVE) {
      subscribe.expire();
    }
  }
}
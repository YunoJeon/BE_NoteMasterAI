package com.be_notemasterai.subscribe.repository;

import com.be_notemasterai.subscribe.entity.Subscribe;
import com.be_notemasterai.subscribe.type.SubscriptionStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

  boolean existsBySubscriberIdAndSubscriptionStatus(Long memberId,
      SubscriptionStatus subscriptionStatus);

  Optional<Subscribe> findByPaymentId(Long id);
}
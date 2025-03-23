package com.be_notemasterai.subscribe.repository;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.subscribe.entity.Subscribe;
import com.be_notemasterai.subscribe.type.SubscriptionStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

  boolean existsBySubscriberIdAndSubscriptionStatus(Long memberId,
      SubscriptionStatus subscriptionStatus);

  Optional<Subscribe> findByPaymentId(Long id);

  @Query("SELECT s.endedAt FROM Subscribe s WHERE s.subscriber = :member AND s.endedAt > CURRENT_TIMESTAMP ORDER BY s.endedAt DESC LIMIT 1")
  LocalDateTime findActiveSubscribeEndedAtBySubscriber(@Param("member") Member member);

  Optional<Subscribe> findBySubscriberAndSubscriptionStatus(Member member, SubscriptionStatus subscriptionStatus);
}
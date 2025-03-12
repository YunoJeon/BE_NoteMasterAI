package com.be_notemasterai.subscribe.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.subscribe.type.SubscriptionStatus;
import com.be_notemasterai.subscribe.type.SubscriptionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "subscribes")
@EntityListeners(AuditingEntityListener.class)
public class Subscribe {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JoinColumn(name = "subscriber_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private Member subscriber;

  @Column(name = "payment_id", nullable = false)
  private Long paymentId;

  @Column(name = "subscription_type", nullable = false)
  @Enumerated(STRING)
  private SubscriptionType subscriptionType;

  @Column(name = "subscription_status", nullable = false)
  @Enumerated(STRING)
  private SubscriptionStatus subscriptionStatus;

  @CreatedDate
  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "ended_at")
  private LocalDateTime endedAt;
}
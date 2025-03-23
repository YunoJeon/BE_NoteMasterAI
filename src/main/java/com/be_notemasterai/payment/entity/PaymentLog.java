package com.be_notemasterai.payment.entity;

import static com.be_notemasterai.payment.type.PaymentStatus.SUCCESS;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.payment.type.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "payment_logs")
public class PaymentLog {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JoinColumn(name = "payment_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private Payment payment;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private Member member;

  @Enumerated(STRING)
  @Column(name = "payment_status", nullable = false)
  private PaymentStatus paymentStatus;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(name = "logged_at", nullable = false)
  private LocalDateTime loggedAt;

  public static PaymentLog of(Member member, Payment payment) {

    PaymentStatus paymentStatus = payment.getPaymentStatus();

    return PaymentLog.builder()
        .payment(payment)
        .member(member)
        .paymentStatus(paymentStatus)
        .amount(paymentStatus == SUCCESS ?
            payment.getAmount() : payment.getRefundedAmount())
        .loggedAt(paymentStatus == SUCCESS ?
            payment.getCreatedAt() : payment.getRefundedAt())
        .build();
  }
}
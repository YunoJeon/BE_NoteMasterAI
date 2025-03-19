package com.be_notemasterai.payment.entity;

import static com.be_notemasterai.payment.type.PaymentStatus.REFUNDED;
import static com.be_notemasterai.payment.type.PaymentStatus.SUCCESS;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.payment.dto.PortoneResponse;
import com.be_notemasterai.payment.type.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
public class Payment {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private Member member;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(name = "refunded_amount")
  private BigDecimal refundedAmount;

  @Column(nullable = false)
  private String currency;

  @Column(name = "payment_method", nullable = false)
  private String paymentMethod;

  @Column(name = "payment_status", nullable = false)
  @Enumerated(STRING)
  private PaymentStatus paymentStatus;

  @Column(name = "imp_uid", nullable = false)
  private String impUid;

  @Column(name = "merchant_uid", nullable = false)
  private String merchantUid;

  private LocalDateTime approvedAt;

  private LocalDateTime refundedAt;

  @CreatedDate
  private LocalDateTime createdAt;

  public static Payment of(Member member, PortoneResponse portoneResponse) {

    return Payment.builder()
        .member(member)
        .amount(portoneResponse.amount())
        .refundedAmount(null)
        .currency(portoneResponse.currency())
        .paymentMethod(portoneResponse.paymentMethod())
        .paymentStatus(SUCCESS)
        .impUid(portoneResponse.impUid())
        .merchantUid(portoneResponse.merchantUid())
        .approvedAt(LocalDateTime.now())
        .refundedAt(null)
        .build();
  }

  public void refund(BigDecimal refundAmount) {

    this.refundedAmount = refundAmount;
    this.paymentStatus = REFUNDED;
    this.refundedAt = LocalDateTime.now();
  }
}
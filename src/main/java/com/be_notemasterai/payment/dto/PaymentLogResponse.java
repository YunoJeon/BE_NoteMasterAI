package com.be_notemasterai.payment.dto;

import com.be_notemasterai.payment.entity.PaymentLog;
import com.be_notemasterai.payment.type.PaymentStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PaymentLogResponse(

    Long paymentLogId,
    Long paymentId,
    PaymentStatus paymentStatus,
    double amount,
    LocalDateTime loggedAt
) {

  public static PaymentLogResponse fromEntity(PaymentLog paymentLog) {

    return PaymentLogResponse.builder()
        .paymentLogId(paymentLog.getId())
        .paymentId(paymentLog.getPayment().getId())
        .paymentStatus(paymentLog.getPaymentStatus())
        .amount(paymentLog.getAmount().doubleValue())
        .loggedAt(paymentLog.getLoggedAt())
        .build();
  }
}
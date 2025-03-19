package com.be_notemasterai.payment.dto;

import com.siot.IamportRestClient.response.Payment;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PortoneResponse(

    BigDecimal amount,

    String currency,

    String paymentMethod,

    String impUid,

    String merchantUid
) {

  public static PortoneResponse of(Payment iamportPayment) {

    return PortoneResponse.builder()
        .amount(iamportPayment.getAmount())
        .currency(iamportPayment.getCurrency())
        .paymentMethod(iamportPayment.getPayMethod())
        .impUid(iamportPayment.getImpUid())
        .merchantUid(iamportPayment.getMerchantUid())
        .build();
  }
}
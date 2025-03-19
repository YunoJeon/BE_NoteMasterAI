package com.be_notemasterai.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(

    double amount,

    String impUid,

    String merchantUid
) {
  public BigDecimal getAmount() {
    return new BigDecimal(amount);
  }
}
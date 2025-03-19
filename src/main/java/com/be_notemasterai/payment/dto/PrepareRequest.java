package com.be_notemasterai.payment.dto;

import com.be_notemasterai.subscribe.type.SubscriptionType;
import java.math.BigDecimal;

public record PrepareRequest(
    String merchantUid,
    double amount,
    SubscriptionType subscriptionType
) {
  public BigDecimal getAmount() {
    return BigDecimal.valueOf(amount);
  }
}
package com.be_notemasterai.payment.service;

import static com.be_notemasterai.exception.ErrorCode.INVALID_AMOUNT;
import static com.be_notemasterai.subscribe.type.SubscriptionType.MONTH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.payment.dto.PrepareRequest;
import com.be_notemasterai.payment.repository.PaymentRepository;
import com.be_notemasterai.subscribe.service.SubscribeService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Prepare;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private PaymentRepository paymentRepository;

  @Mock
  private IamportClient iamportClient;

  @Mock
  private SubscribeService subscribeService;

  @InjectMocks
  private PaymentService paymentService;

  private final String merchantUid = "order_123456";
  private final String impUid = "imp_123456";
  private final double amount = 9900;

  private BigDecimal getAmount() {
    return BigDecimal.valueOf(amount);
  }

  private void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  @Test
  @DisplayName("사전 결제 검증이 성공한다")
  void prepare_payment_success() throws Exception {
    // given
    PrepareRequest prepareRequest = new PrepareRequest(merchantUid, amount, MONTH);
    IamportResponse<Prepare> response = new IamportResponse<>();
    Prepare prepare = new Prepare();

    setField(response, "code", 0);

    setField(response, "response", prepare);

    when(iamportClient.postPrepare(any())).thenReturn(response);
    // when
    // then
    assertDoesNotThrow(() -> paymentService.preparePayment(prepareRequest));
  }

  @Test
  @DisplayName("결제 금액이 일치하지 않으면 예외가 발생한다")
  void prepare_payment_failure_invalid_amount() {
    // given
    PrepareRequest prepareRequest = new PrepareRequest(merchantUid, 10000.0, MONTH);

    // when
    CustomException e = assertThrows(CustomException.class,
        () -> paymentService.preparePayment(prepareRequest));
    // then
    assertEquals(e.getErrorCode(), INVALID_AMOUNT);
  }
}
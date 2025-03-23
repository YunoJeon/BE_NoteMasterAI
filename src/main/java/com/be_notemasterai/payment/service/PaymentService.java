package com.be_notemasterai.payment.service;

import static com.be_notemasterai.exception.ErrorCode.ALREADY_REFUNDED;
import static com.be_notemasterai.exception.ErrorCode.FAILED_REFUND_PAYMENT;
import static com.be_notemasterai.exception.ErrorCode.FAILED_VALID_PAYMENT;
import static com.be_notemasterai.exception.ErrorCode.INVALID_AMOUNT;
import static com.be_notemasterai.exception.ErrorCode.NOT_FOUND_PAYMENT;
import static com.be_notemasterai.exception.ErrorCode.PAYMENT_OWNER_MISMATCH;
import static com.be_notemasterai.exception.ErrorCode.STATUS_NOT_PAID;
import static com.be_notemasterai.payment.type.PaymentStatus.REFUNDED;
import static com.be_notemasterai.subscribe.type.SubscriptionType.MONTH;
import static com.be_notemasterai.subscribe.type.SubscriptionType.YEAR;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.payment.dto.PaymentLogResponse;
import com.be_notemasterai.payment.dto.PaymentRequest;
import com.be_notemasterai.payment.dto.PortoneResponse;
import com.be_notemasterai.payment.dto.PrepareRequest;
import com.be_notemasterai.payment.dto.RefundRequest;
import com.be_notemasterai.payment.entity.Payment;
import com.be_notemasterai.payment.entity.PaymentLog;
import com.be_notemasterai.payment.repository.PaymentLogRepository;
import com.be_notemasterai.payment.repository.PaymentRepository;
import com.be_notemasterai.subscribe.service.SubscribeService;
import com.be_notemasterai.subscribe.type.SubscriptionType;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Prepare;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

  private final IamportClient iamportClient;

  private final PaymentRepository paymentRepository;

  private final SubscribeService subscribeService;

  private final PaymentLogRepository paymentLogRepository;

  public void preparePayment(PrepareRequest prepareRequest) {

    if (!isValidAmount(prepareRequest.getAmount(), prepareRequest.subscriptionType())) {
      throw new CustomException(INVALID_AMOUNT);
    }

    PrepareData prepareData = new PrepareData(prepareRequest.merchantUid(),
        prepareRequest.getAmount());

    IamportResponse<Prepare> iamportResponse;
    try {
      iamportResponse = iamportClient.postPrepare(prepareData);

    } catch (Exception e) {
      throw new CustomException(FAILED_VALID_PAYMENT);
    }

    if (iamportResponse.getCode() != 0) {
      throw new CustomException(FAILED_VALID_PAYMENT);
    }
  }

  private boolean isValidAmount(BigDecimal amount, SubscriptionType subscriptionType) {
    return (subscriptionType == MONTH && amount.compareTo(BigDecimal.valueOf(9900)) == 0) ||
           (subscriptionType == YEAR && amount.compareTo(BigDecimal.valueOf(99000)) == 0);
  }

  @Transactional
  public void verifyAndSavePayment(Member member, PaymentRequest paymentRequest) {

    try {
      IamportResponse<com.siot.IamportRestClient.response.Payment> response = iamportClient.paymentByImpUid(
          paymentRequest.impUid());
      com.siot.IamportRestClient.response.Payment iamportPayment = getPayment(paymentRequest,
          response);

      PortoneResponse portoneResponse = PortoneResponse.of(iamportPayment);

      Payment payment = Payment.of(member, portoneResponse);

      paymentRepository.save(payment);

      logPayment(member, payment);

      SubscriptionType subscriptionType = determineSubscriptionType(payment.getAmount());

      subscribeService.createSubscription(member, payment, subscriptionType);

    } catch (Exception e) {
      CancelData cancelData = new CancelData(paymentRequest.impUid(), true, paymentRequest.getAmount());
      cancelData.setReason("결제 검증 실패로 인한 자동 환불");
      cancelPayment(cancelData);
      throw new CustomException(FAILED_VALID_PAYMENT);
    }
  }

  private void cancelPayment(CancelData cancelData) {

    try {
      iamportClient.cancelPaymentByImpUid(cancelData);
    } catch (Exception e) {
      throw new CustomException(FAILED_REFUND_PAYMENT);
    }
  }

  private static com.siot.IamportRestClient.response.Payment getPayment(
      PaymentRequest paymentRequest,
      IamportResponse<com.siot.IamportRestClient.response.Payment> response) {
    com.siot.IamportRestClient.response.Payment iamportPayment = response.getResponse();

    if (iamportPayment == null) {
      throw new CustomException(FAILED_VALID_PAYMENT);
    }

    if (!iamportPayment.getMerchantUid().equals(paymentRequest.merchantUid())) {
      throw new CustomException(FAILED_VALID_PAYMENT);
    }

    if (!"paid".equals(iamportPayment.getStatus())) {
      throw new CustomException(STATUS_NOT_PAID);
    }

    if (!iamportPayment.getAmount().equals(paymentRequest.getAmount())) {
      throw new CustomException(INVALID_AMOUNT);
    }

    return iamportPayment;
  }

  private SubscriptionType determineSubscriptionType(BigDecimal amount) {

    if (amount.compareTo(BigDecimal.valueOf(9900)) == 0) {
      return MONTH;
    } else if (amount.compareTo(BigDecimal.valueOf(99000)) == 0) {
      return YEAR;
    } else {
      throw new CustomException(INVALID_AMOUNT);
    }
  }

  @Transactional
  public void refundPayment(Member member, RefundRequest refundRequest) {

    Payment payment = paymentRepository.findById(refundRequest.paymentId())
        .orElseThrow(() -> new CustomException(NOT_FOUND_PAYMENT));

    if (!payment.getMember().getId().equals(member.getId())) {
      throw new CustomException(PAYMENT_OWNER_MISMATCH);
    }

    if (payment.getPaymentStatus() == REFUNDED) {
      throw new CustomException(ALREADY_REFUNDED);
    }

    long daysUsed = Duration.between(payment.getApprovedAt(), LocalDateTime.now()).toDays();
    BigDecimal refundAmount = calculateRefundAmount(payment.getAmount(), daysUsed);

    CancelData cancelData = new CancelData(payment.getImpUid(), true, refundAmount);
    cancelData.setReason("사용자 요청 환불");

    cancelPayment(cancelData);

    payment.refund(refundAmount);

    logPayment(member, payment);

    subscribeService.cancelSubscription(payment);
  }

  public BigDecimal calculateRefundAmount(BigDecimal amount, long daysUsed) {

    if (daysUsed < 1) {
      return amount;
    }

    BigDecimal dailyRate = amount.compareTo(BigDecimal.valueOf(9900)) == 0 ?
        BigDecimal.valueOf(9900).divide(BigDecimal.valueOf(30), 2, HALF_UP) :
        BigDecimal.valueOf(99000).divide(BigDecimal.valueOf(365), 2, HALF_UP);

    BigDecimal usedAmount = dailyRate.multiply(BigDecimal.valueOf(daysUsed));
    BigDecimal refundAmount = amount.subtract(usedAmount);

    return refundAmount.max(ZERO);
  }

  private void logPayment(Member member, Payment payment) {

    PaymentLog paymentLog = PaymentLog.of(member, payment);

    paymentLogRepository.save(paymentLog);
  }

  public Page<PaymentLogResponse> getPaymentLog(Member member, Pageable pageable) {

    Page<PaymentLog> logs = paymentLogRepository.findByMember(member, pageable);

    return logs.map(PaymentLogResponse::fromEntity);
  }
}
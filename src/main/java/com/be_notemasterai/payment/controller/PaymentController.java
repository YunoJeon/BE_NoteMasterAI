package com.be_notemasterai.payment.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.payment.dto.PaymentLogResponse;
import com.be_notemasterai.payment.dto.PaymentRequest;
import com.be_notemasterai.payment.dto.PrepareRequest;
import com.be_notemasterai.payment.dto.RefundRequest;
import com.be_notemasterai.payment.service.PaymentService;
import com.be_notemasterai.security.resolver.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping("/prepare")
  public ResponseEntity<Void> preparePayment(@RequestBody PrepareRequest prepareRequest) {

    paymentService.preparePayment(prepareRequest);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/verify")
  public ResponseEntity<Void> verifyAndSavePayment(@CurrentMember Member member,
      @RequestBody PaymentRequest paymentRequest) {

    paymentService.verifyAndSavePayment(member, paymentRequest);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/refund")
  public ResponseEntity<Void> refundPayment(@CurrentMember Member member,
      @RequestBody RefundRequest refundRequest) {

    paymentService.refundPayment(member, refundRequest);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/logs")
  public ResponseEntity<Page<PaymentLogResponse>> getPaymentLog(@CurrentMember Member member,
      @PageableDefault(sort = "loggedAt", direction = DESC) Pageable pageable) {

    return ResponseEntity.ok(paymentService.getPaymentLog(member, pageable));
  }
}
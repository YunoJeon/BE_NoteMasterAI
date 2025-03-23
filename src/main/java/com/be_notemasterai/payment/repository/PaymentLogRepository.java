package com.be_notemasterai.payment.repository;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.payment.entity.PaymentLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

  Page<PaymentLog> findByMember(Member member, Pageable pageable);
}
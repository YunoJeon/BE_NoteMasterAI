package com.be_notemasterai.member.repository;

import com.be_notemasterai.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByProviderUuid(String providerUuid);

  boolean existsByTag(String tag);

  boolean existsByNickname(String nickname);

  Optional<Member> findByTag(String tag);
}
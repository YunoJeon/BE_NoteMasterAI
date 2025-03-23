package com.be_notemasterai.member.repository;

import com.be_notemasterai.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByProviderUuid(String providerUuid);

  boolean existsByTag(String tag);

  boolean existsByNickname(String nickname);

  @Query("SELECT m FROM Member m WHERE m.tag = :tag AND m.deletedAt IS NULL")
  Optional<Member> findByTag(@Param("tag") String tag);
}
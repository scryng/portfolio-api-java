package com.scryng.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scryng.portfolio.domain.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByExternalId(Long externalId);

}

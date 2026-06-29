package com.scryng.portfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scryng.portfolio.client.MemberApiClient;
import com.scryng.portfolio.domain.entity.Member;
import com.scryng.portfolio.domain.exception.ResourceNotFoundException;
import com.scryng.portfolio.dto.response.ExternalMemberResponse;
import com.scryng.portfolio.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberIntegrationService {

	private final MemberApiClient memberApiClient;
	private final MemberRepository memberRepository;

	@Transactional
	public Member syncMember(Long externalId) {
		return memberRepository.findByExternalId(externalId)
				.orElseGet(() -> createFromExternal(externalId));
	}

	private Member createFromExternal(Long externalId) {
		ExternalMemberResponse external = memberApiClient.findById(externalId);
		Member member = new Member();
		member.setExternalId(external.id());
		member.setName(external.name());
		member.setRole(external.role());
		return memberRepository.save(member);
	}

	@Transactional(readOnly = true)
	public Member findById(Long id) {
		return memberRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
	}

}

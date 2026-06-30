package com.scryng.portfolio.mock;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.scryng.portfolio.domain.enums.MemberRole;
import com.scryng.portfolio.dto.request.ExternalMemberRequest;
import com.scryng.portfolio.dto.response.ExternalMemberResponse;

@Component
public class MockExternalMemberStore {

	private final AtomicLong idSequence = new AtomicLong(1);
	private final Map<Long, ExternalMemberResponse> members = new ConcurrentHashMap<>();

	public ExternalMemberResponse create(ExternalMemberRequest request) {
		Long id = idSequence.getAndIncrement();
		ExternalMemberResponse response = new ExternalMemberResponse(id, request.name(), request.role());
		members.put(id, response);
		return response;
	}

	public Optional<ExternalMemberResponse> findById(Long id) {
		return Optional.ofNullable(members.get(id));
	}

}

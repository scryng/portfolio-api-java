package com.scryng.portfolio.client;

import org.springframework.stereotype.Component;

import com.scryng.portfolio.domain.exception.ResourceNotFoundException;
import com.scryng.portfolio.dto.response.ExternalMemberResponse;
import com.scryng.portfolio.mock.MockExternalMemberStore;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InProcessMemberApiClient implements MemberApiClient {

	private final MockExternalMemberStore store;

	@Override
	public ExternalMemberResponse findById(Long externalId) {
		return store.findById(externalId)
				.orElseThrow(() -> new ResourceNotFoundException("External member not found: " + externalId));
	}

}

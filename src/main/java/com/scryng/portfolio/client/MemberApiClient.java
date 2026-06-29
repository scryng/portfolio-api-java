package com.scryng.portfolio.client;

import com.scryng.portfolio.dto.response.ExternalMemberResponse;

public interface MemberApiClient {

	ExternalMemberResponse findById(Long externalId);

}

package com.scryng.portfolio.dto.response;

import com.scryng.portfolio.domain.enums.MemberRole;

public record ExternalMemberResponse(
		Long id,
		String name,
		MemberRole role) {
}

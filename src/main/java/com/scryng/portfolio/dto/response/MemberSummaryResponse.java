package com.scryng.portfolio.dto.response;

import com.scryng.portfolio.domain.enums.MemberRole;

public record MemberSummaryResponse(
		Long id,
		Long externalId,
		String name,
		MemberRole role) {
}

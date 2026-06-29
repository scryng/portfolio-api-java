package com.scryng.portfolio.dto.request;

import com.scryng.portfolio.domain.enums.MemberRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExternalMemberRequest(
		@NotBlank @Size(max = 150) String name,
		@NotNull MemberRole role) {
}

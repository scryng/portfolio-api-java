package com.scryng.portfolio.dto.request;

import com.scryng.portfolio.domain.enums.ProjectStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateProjectStatusRequest(
		@NotNull ProjectStatus status) {
}

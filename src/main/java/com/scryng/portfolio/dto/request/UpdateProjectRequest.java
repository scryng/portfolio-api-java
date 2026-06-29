package com.scryng.portfolio.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
		@NotBlank @Size(max = 200) String name,
		@NotNull LocalDate startDate,
		@NotNull LocalDate expectedEndDate,
		LocalDate actualEndDate,
		@NotNull @DecimalMin("0.01") BigDecimal totalBudget,
		String description,
		@NotNull Long managerExternalId,
		@NotEmpty @Size(min = 1, max = 10) List<Long> memberExternalIds) {
}

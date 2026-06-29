package com.scryng.portfolio.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.scryng.portfolio.domain.enums.ProjectStatus;

public record PortfolioReportResponse(
		List<StatusSummaryResponse> summaries,
		Double averageClosedProjectDurationDays) {

	public record StatusSummaryResponse(
			ProjectStatus status,
			long projectCount,
			BigDecimal totalBudget) {
	}

}

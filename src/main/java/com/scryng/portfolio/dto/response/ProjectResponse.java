package com.scryng.portfolio.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.scryng.portfolio.domain.enums.ProjectStatus;
import com.scryng.portfolio.domain.enums.RiskLevel;

public record ProjectResponse(
		Long id,
		String name,
		LocalDate startDate,
		LocalDate expectedEndDate,
		LocalDate actualEndDate,
		BigDecimal totalBudget,
		String description,
		MemberSummaryResponse manager,
		List<MemberSummaryResponse> members,
		ProjectStatus status,
		RiskLevel riskLevel) {
}

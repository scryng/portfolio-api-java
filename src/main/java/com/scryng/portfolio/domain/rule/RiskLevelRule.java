package com.scryng.portfolio.domain.rule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.scryng.portfolio.domain.enums.RiskLevel;

public final class RiskLevelRule {

	private static final BigDecimal BUDGET_LOW_MAX = new BigDecimal("100000.00");
	private static final BigDecimal BUDGET_MEDIUM_MAX = new BigDecimal("500000.00");
	private static final long MONTHS_LOW_MAX = 3;
	private static final long MONTHS_MEDIUM_MAX = 6;

	private RiskLevelRule() {
	}

	public static RiskLevel calculate(BigDecimal totalBudget, LocalDate startDate, LocalDate expectedEndDate) {
		long months = ChronoUnit.MONTHS.between(startDate, expectedEndDate);

		if (totalBudget.compareTo(BUDGET_MEDIUM_MAX) > 0 || months > MONTHS_MEDIUM_MAX) {
			return RiskLevel.HIGH;
		}
		if (totalBudget.compareTo(BUDGET_LOW_MAX) > 0 || months > MONTHS_LOW_MAX) {
			return RiskLevel.MEDIUM;
		}
		return RiskLevel.LOW;
	}

}

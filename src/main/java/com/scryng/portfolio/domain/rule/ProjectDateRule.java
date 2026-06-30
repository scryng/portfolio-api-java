package com.scryng.portfolio.domain.rule;

import java.time.LocalDate;

import com.scryng.portfolio.domain.exception.BusinessException;

public final class ProjectDateRule {

	private ProjectDateRule() {
	}

	public static void validate(LocalDate startDate, LocalDate expectedEndDate) {
		if (expectedEndDate.isBefore(startDate)) {
			throw new BusinessException("Expected end date must be on or after start date");
		}
	}

}

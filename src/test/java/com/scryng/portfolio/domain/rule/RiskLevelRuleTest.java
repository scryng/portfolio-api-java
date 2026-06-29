package com.scryng.portfolio.domain.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.scryng.portfolio.domain.enums.RiskLevel;
import com.scryng.portfolio.domain.exception.BusinessException;

class RiskLevelRuleTest {

	@Test
	void shouldReturnLowRisk() {
		RiskLevel risk = RiskLevelRule.calculate(
				new BigDecimal("50000.00"),
				LocalDate.of(2026, 1, 1),
				LocalDate.of(2026, 3, 1));

		assertThat(risk).isEqualTo(RiskLevel.LOW);
	}

	@Test
	void shouldReturnMediumRiskByBudget() {
		RiskLevel risk = RiskLevelRule.calculate(
				new BigDecimal("200000.00"),
				LocalDate.of(2026, 1, 1),
				LocalDate.of(2026, 2, 1));

		assertThat(risk).isEqualTo(RiskLevel.MEDIUM);
	}

	@Test
	void shouldReturnHighRiskByDuration() {
		RiskLevel risk = RiskLevelRule.calculate(
				new BigDecimal("50000.00"),
				LocalDate.of(2026, 1, 1),
				LocalDate.of(2026, 8, 1));

		assertThat(risk).isEqualTo(RiskLevel.HIGH);
	}

	@Test
	void shouldRejectInvalidDateRangeInProjectDateRule() {
		assertThatThrownBy(() -> ProjectDateRule.validate(
				LocalDate.of(2026, 5, 1),
				LocalDate.of(2026, 1, 1)))
				.isInstanceOf(BusinessException.class);
	}

}

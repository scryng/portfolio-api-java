package com.scryng.portfolio.domain.rule;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.scryng.portfolio.domain.enums.ProjectStatus;
import com.scryng.portfolio.domain.exception.InvalidStatusTransitionException;

class ProjectStatusTransitionRuleTest {

	@Test
	void shouldAllowSequentialTransition() {
		assertThatCode(() -> ProjectStatusTransitionRule.validate(
				ProjectStatus.UNDER_ANALYSIS, ProjectStatus.ANALYSIS_COMPLETED))
				.doesNotThrowAnyException();
	}

	@Test
	void shouldAllowCancelFromAnyActiveStatus() {
		assertThatCode(() -> ProjectStatusTransitionRule.validate(
				ProjectStatus.IN_PROGRESS, ProjectStatus.CANCELLED))
				.doesNotThrowAnyException();
	}

	@Test
	void shouldRejectSkippedStep() {
		assertThatThrownBy(() -> ProjectStatusTransitionRule.validate(
				ProjectStatus.UNDER_ANALYSIS, ProjectStatus.STARTED))
				.isInstanceOf(InvalidStatusTransitionException.class);
	}

	@Test
	void shouldRejectTransitionFromClosed() {
		assertThatThrownBy(() -> ProjectStatusTransitionRule.validate(
				ProjectStatus.CLOSED, ProjectStatus.IN_PROGRESS))
				.isInstanceOf(InvalidStatusTransitionException.class);
	}

}

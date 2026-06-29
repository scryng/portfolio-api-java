package com.scryng.portfolio.domain.rule;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.scryng.portfolio.domain.entity.Project;
import com.scryng.portfolio.domain.enums.ProjectStatus;
import com.scryng.portfolio.domain.exception.ProjectDeletionNotAllowedException;

class ProjectDeletionRuleTest {

	@Test
	void shouldAllowDeleteDuringAnalysis() {
		Project project = new Project();
		project.setStatus(ProjectStatus.UNDER_ANALYSIS);

		assertThatCode(() -> ProjectDeletionRule.validate(project)).doesNotThrowAnyException();
	}

	@Test
	void shouldBlockDeleteWhenStarted() {
		Project project = new Project();
		project.setStatus(ProjectStatus.STARTED);

		assertThatThrownBy(() -> ProjectDeletionRule.validate(project))
				.isInstanceOf(ProjectDeletionNotAllowedException.class);
	}

}

package com.scryng.portfolio.domain.rule;

import com.scryng.portfolio.domain.entity.Project;
import com.scryng.portfolio.domain.exception.ProjectDeletionNotAllowedException;

public final class ProjectDeletionRule {

	private ProjectDeletionRule() {
	}

	public static void validate(Project project) {
		if (!project.getStatus().isDeletable()) {
			throw new ProjectDeletionNotAllowedException(
					"Project cannot be deleted when status is " + project.getStatus());
		}
	}

}

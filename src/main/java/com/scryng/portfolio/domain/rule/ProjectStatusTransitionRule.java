package com.scryng.portfolio.domain.rule;

import com.scryng.portfolio.domain.enums.ProjectStatus;
import com.scryng.portfolio.domain.exception.InvalidStatusTransitionException;

public final class ProjectStatusTransitionRule {

	private ProjectStatusTransitionRule() {
	}

	public static void validate(ProjectStatus current, ProjectStatus target) {
		if (current == target) {
			return;
		}
		if (target == ProjectStatus.CANCELLED) {
			return;
		}
		if (current == ProjectStatus.CANCELLED || current == ProjectStatus.CLOSED) {
			throw new InvalidStatusTransitionException(
					"Cannot transition from status " + current + " to " + target);
		}
		ProjectStatus expectedNext = nextInSequence(current);
		if (expectedNext != target) {
			throw new InvalidStatusTransitionException(
					"Invalid status transition from " + current + " to " + target
							+ ". Expected next status: " + expectedNext);
		}
	}

	private static ProjectStatus nextInSequence(ProjectStatus current) {
		return switch (current) {
			case UNDER_ANALYSIS -> ProjectStatus.ANALYSIS_COMPLETED;
			case ANALYSIS_COMPLETED -> ProjectStatus.ANALYSIS_APPROVED;
			case ANALYSIS_APPROVED -> ProjectStatus.STARTED;
			case STARTED -> ProjectStatus.PLANNED;
			case PLANNED -> ProjectStatus.IN_PROGRESS;
			case IN_PROGRESS -> ProjectStatus.CLOSED;
			case CLOSED, CANCELLED -> throw new InvalidStatusTransitionException(
					"No sequential transition available from " + current);
		};
	}

}

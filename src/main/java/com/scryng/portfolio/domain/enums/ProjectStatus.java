package com.scryng.portfolio.domain.enums;

public enum ProjectStatus {
	UNDER_ANALYSIS,
	ANALYSIS_COMPLETED,
	ANALYSIS_APPROVED,
	STARTED,
	PLANNED,
	IN_PROGRESS,
	CLOSED,
	CANCELLED;

	public boolean isDeletable() {
		return this != STARTED && this != IN_PROGRESS && this != CLOSED;
	}

	public boolean isActive() {
		return this != CLOSED && this != CANCELLED;
	}

}

package com.scryng.portfolio.domain.enums;

public enum ProjectStatus {
	UNDER_ANALYSIS, // em análise
	ANALYSIS_COMPLETED, // análise realizada
	ANALYSIS_APPROVED, // análise aprovada
	STARTED, // iniciado
	PLANNED, // planejado
	IN_PROGRESS, // em andamento
	CLOSED, // encerrado
	CANCELLED; // cancelado

	public boolean isDeletable() {
		return this != STARTED && this != IN_PROGRESS && this != CLOSED;
	}

	public boolean isActive() {
		return this != CLOSED && this != CANCELLED;
	}

}

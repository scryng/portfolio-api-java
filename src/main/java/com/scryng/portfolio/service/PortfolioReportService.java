package com.scryng.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scryng.portfolio.dto.response.PortfolioReportResponse;
import com.scryng.portfolio.dto.response.PortfolioReportResponse.StatusSummaryResponse;
import com.scryng.portfolio.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioReportService {

	private final ProjectRepository projectRepository;

	@Transactional(readOnly = true)
	public PortfolioReportResponse generate() {
		List<StatusSummaryResponse> summaries = projectRepository.summarizeByStatus().stream()
				.map(row -> new StatusSummaryResponse(
						row.getStatus(), row.getProjectCount(), row.getTotalBudget()))
				.toList();

		Double averageDuration = projectRepository.averageClosedProjectDurationDays();

		return new PortfolioReportResponse(summaries, averageDuration);
	}

}

package com.scryng.portfolio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scryng.portfolio.domain.enums.ProjectStatus;
import com.scryng.portfolio.repository.ProjectRepository;
import com.scryng.portfolio.repository.ProjectRepository.StatusSummaryProjection;

@ExtendWith(MockitoExtension.class)
class PortfolioReportServiceTest {

	@Mock
	private ProjectRepository projectRepository;

	@InjectMocks
	private PortfolioReportService portfolioReportService;

	@Test
	void shouldGeneratePortfolioReport() {
		StatusSummaryProjection summary = new StatusSummaryProjection() {
			@Override
			public ProjectStatus getStatus() {
				return ProjectStatus.CLOSED;
			}

			@Override
			public long getProjectCount() {
				return 2;
			}

			@Override
			public BigDecimal getTotalBudget() {
				return new BigDecimal("150000.00");
			}
		};

		when(projectRepository.summarizeByStatus()).thenReturn(List.of(summary));
		when(projectRepository.averageClosedProjectDurationDays()).thenReturn(90.0);

		var report = portfolioReportService.generate();

		assertThat(report.summaries()).hasSize(1);
		assertThat(report.summaries().get(0).projectCount()).isEqualTo(2);
		assertThat(report.averageClosedProjectDurationDays()).isEqualTo(90.0);
	}

}

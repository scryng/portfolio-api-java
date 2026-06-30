package com.scryng.portfolio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.scryng.portfolio.domain.entity.Member;
import com.scryng.portfolio.domain.entity.Project;
import com.scryng.portfolio.domain.enums.MemberRole;
import com.scryng.portfolio.domain.enums.ProjectStatus;
import com.scryng.portfolio.domain.exception.InvalidStatusTransitionException;
import com.scryng.portfolio.domain.exception.ProjectDeletionNotAllowedException;
import com.scryng.portfolio.domain.exception.ResourceNotFoundException;
import com.scryng.portfolio.dto.request.CreateProjectRequest;
import com.scryng.portfolio.dto.request.UpdateProjectRequest;
import com.scryng.portfolio.dto.request.UpdateProjectStatusRequest;
import com.scryng.portfolio.dto.response.PortfolioReportResponse;
import com.scryng.portfolio.dto.response.ProjectResponse;
import com.scryng.portfolio.mapper.ProjectMapper;
import com.scryng.portfolio.repository.ProjectRepository;
import com.scryng.portfolio.repository.ProjectRepository.StatusSummaryProjection;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private MemberIntegrationService memberIntegrationService;

	@Mock
	private ProjectMapper projectMapper;

	@InjectMocks
	private ProjectService projectService;

	@Test
	void shouldCreateProjectWithDefaultStatus() {
		Member manager = employee(1L, 10L);
		Member member = employee(2L, 11L);

		when(memberIntegrationService.syncMember(10L)).thenReturn(manager);
		when(memberIntegrationService.syncMember(11L)).thenReturn(member);
		when(projectRepository.countActiveProjectsByMemberId(2L)).thenReturn(0L);
		when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
			Project project = invocation.getArgument(0);
			project.setId(1L);
			return project;
		});
		when(projectMapper.toResponse(any(Project.class))).thenReturn(sampleResponse());

		CreateProjectRequest request = new CreateProjectRequest(
				"Project",
				LocalDate.of(2026, 1, 1),
				LocalDate.of(2026, 3, 1),
				null,
				new BigDecimal("50000"),
				"desc",
				10L,
				List.of(11L),
				null);

		ProjectResponse response = projectService.create(request);

		assertThat(response.id()).isEqualTo(1L);
		verify(projectRepository).save(any(Project.class));
	}

	@Test
	void shouldFindProjectById() {
		Project project = projectWithStatus(ProjectStatus.UNDER_ANALYSIS);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(projectMapper.toResponse(project)).thenReturn(sampleResponse());

		assertThat(projectService.findById(1L).id()).isEqualTo(1L);
	}

	@Test
	void shouldThrowWhenProjectNotFound() {
		when(projectRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> projectService.findById(99L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldListProjectsWithStatusFilter() {
		Project project = projectWithStatus(ProjectStatus.UNDER_ANALYSIS);
		Page<Project> page = new PageImpl<>(List.of(project));
		when(projectRepository.findByStatus(eq(ProjectStatus.UNDER_ANALYSIS), any(Pageable.class)))
				.thenReturn(page);
		when(projectMapper.toResponse(project)).thenReturn(sampleResponse());

		Page<ProjectResponse> result = projectService.findAll(ProjectStatus.UNDER_ANALYSIS, Pageable.unpaged());

		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	void shouldUpdateProject() {
		Project project = projectWithStatus(ProjectStatus.UNDER_ANALYSIS);
		Member manager = employee(1L, 10L);
		Member member = employee(2L, 11L);

		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(memberIntegrationService.syncMember(10L)).thenReturn(manager);
		when(memberIntegrationService.syncMember(11L)).thenReturn(member);
		when(projectRepository.countActiveProjectsByMemberIdExcludingProject(2L, 1L)).thenReturn(0L);
		when(projectRepository.save(project)).thenReturn(project);
		when(projectMapper.toResponse(project)).thenReturn(sampleResponse());

		UpdateProjectRequest request = new UpdateProjectRequest(
				"Updated",
				LocalDate.of(2026, 1, 1),
				LocalDate.of(2026, 3, 1),
				null,
				new BigDecimal("60000"),
				"updated",
				10L,
				List.of(11L));

		assertThat(projectService.update(1L, request).id()).isEqualTo(1L);
	}

	@Test
	void shouldUpdateStatusSequentiallyAndSetActualEndDateOnClose() {
		Project project = projectWithStatus(ProjectStatus.IN_PROGRESS);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(projectRepository.save(project)).thenReturn(project);
		when(projectMapper.toResponse(project)).thenReturn(sampleResponse());

		projectService.updateStatus(1L, new UpdateProjectStatusRequest(ProjectStatus.CLOSED));

		assertThat(project.getStatus()).isEqualTo(ProjectStatus.CLOSED);
		assertThat(project.getActualEndDate()).isNotNull();
	}

	@Test
	void shouldRejectInvalidStatusTransition() {
		Project project = projectWithStatus(ProjectStatus.UNDER_ANALYSIS);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

		assertThatThrownBy(() -> projectService.updateStatus(
				1L, new UpdateProjectStatusRequest(ProjectStatus.STARTED)))
				.isInstanceOf(InvalidStatusTransitionException.class);
	}

	@Test
	void shouldRejectDeleteWhenNotAllowed() {
		Project project = projectWithStatus(ProjectStatus.IN_PROGRESS);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

		assertThatThrownBy(() -> projectService.delete(1L))
				.isInstanceOf(ProjectDeletionNotAllowedException.class);
	}

	@Test
	void shouldDeleteWhenAllowed() {
		Project project = projectWithStatus(ProjectStatus.UNDER_ANALYSIS);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

		projectService.delete(1L);

		verify(projectRepository).delete(project);
	}

	private Project projectWithStatus(ProjectStatus status) {
		Project project = new Project();
		project.setId(1L);
		project.setName("Project");
		project.setStartDate(LocalDate.of(2026, 1, 1));
		project.setExpectedEndDate(LocalDate.of(2026, 3, 1));
		project.setTotalBudget(new BigDecimal("50000"));
		project.setStatus(status);
		return project;
	}

	private ProjectResponse sampleResponse() {
		return new ProjectResponse(1L, "Project", LocalDate.now(), LocalDate.now().plusMonths(2),
				null, new BigDecimal("50000"), "desc",
				null, List.of(), ProjectStatus.UNDER_ANALYSIS, null);
	}

	private Member employee(Long id, Long externalId) {
		Member member = new Member();
		member.setId(id);
		member.setExternalId(externalId);
		member.setName("Employee " + id);
		member.setRole(MemberRole.EMPLOYEE);
		return member;
	}

}

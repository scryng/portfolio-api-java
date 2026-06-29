package com.scryng.portfolio.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scryng.portfolio.domain.entity.Member;
import com.scryng.portfolio.domain.entity.Project;
import com.scryng.portfolio.domain.enums.ProjectStatus;
import com.scryng.portfolio.domain.exception.ResourceNotFoundException;
import com.scryng.portfolio.domain.rule.ProjectDateRule;
import com.scryng.portfolio.domain.rule.ProjectDeletionRule;
import com.scryng.portfolio.domain.rule.ProjectMemberAllocationRule;
import com.scryng.portfolio.domain.rule.ProjectStatusTransitionRule;
import com.scryng.portfolio.dto.request.CreateProjectRequest;
import com.scryng.portfolio.dto.request.UpdateProjectRequest;
import com.scryng.portfolio.dto.request.UpdateProjectStatusRequest;
import com.scryng.portfolio.dto.response.ProjectResponse;
import com.scryng.portfolio.mapper.ProjectMapper;
import com.scryng.portfolio.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final MemberIntegrationService memberIntegrationService;
	private final ProjectMapper projectMapper;

	@Transactional
	public ProjectResponse create(CreateProjectRequest request) {
		ProjectDateRule.validate(request.startDate(), request.expectedEndDate());

		Member manager = memberIntegrationService.syncMember(request.managerExternalId());
		Set<Member> members = resolveAndValidateMembers(request.memberExternalIds(), null);

		Project project = new Project();
		applyFields(project, request.name(), request.startDate(), request.expectedEndDate(),
				request.actualEndDate(), request.totalBudget(), request.description(), manager, members);
		project.setStatus(request.status() != null ? request.status() : ProjectStatus.UNDER_ANALYSIS);

		return projectMapper.toResponse(projectRepository.save(project));
	}

	@Transactional(readOnly = true)
	public ProjectResponse findById(Long id) {
		return projectMapper.toResponse(getProject(id));
	}

	@Transactional(readOnly = true)
	public Page<ProjectResponse> findAll(ProjectStatus status, Pageable pageable) {
		Page<Project> page = status != null
				? projectRepository.findByStatus(status, pageable)
				: projectRepository.findAll(pageable);
		return page.map(projectMapper::toResponse);
	}

	@Transactional
	public ProjectResponse update(Long id, UpdateProjectRequest request) {
		ProjectDateRule.validate(request.startDate(), request.expectedEndDate());
		Project project = getProject(id);

		Member manager = memberIntegrationService.syncMember(request.managerExternalId());
		Set<Member> members = resolveAndValidateMembers(request.memberExternalIds(), project.getId());

		applyFields(project, request.name(), request.startDate(), request.expectedEndDate(),
				request.actualEndDate(), request.totalBudget(), request.description(), manager, members);

		return projectMapper.toResponse(projectRepository.save(project));
	}

	@Transactional
	public ProjectResponse updateStatus(Long id, UpdateProjectStatusRequest request) {
		Project project = getProject(id);
		ProjectStatusTransitionRule.validate(project.getStatus(), request.status());

		if (request.status() == ProjectStatus.CLOSED && project.getActualEndDate() == null) {
			project.setActualEndDate(java.time.LocalDate.now());
		}

		project.setStatus(request.status());
		return projectMapper.toResponse(projectRepository.save(project));
	}

	@Transactional
	public void delete(Long id) {
		Project project = getProject(id);
		ProjectDeletionRule.validate(project);
		projectRepository.delete(project);
	}

	private Project getProject(Long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
	}

	private Set<Member> resolveAndValidateMembers(List<Long> externalIds, Long projectId) {
		Set<Member> members = new HashSet<>();
		for (Long externalId : externalIds) {
			Member member = memberIntegrationService.syncMember(externalId);
			long activeCount = projectId == null
					? projectRepository.countActiveProjectsByMemberId(member.getId())
					: projectRepository.countActiveProjectsByMemberIdExcludingProject(member.getId(), projectId);
			ProjectMemberAllocationRule.validateActiveProjectCount(member, activeCount);
			members.add(member);
		}
		ProjectMemberAllocationRule.validateTeamSize(members);
		ProjectMemberAllocationRule.validateMemberRoles(members);
		return members;
	}

	private void applyFields(Project project, String name, java.time.LocalDate startDate,
			java.time.LocalDate expectedEndDate, java.time.LocalDate actualEndDate,
			java.math.BigDecimal totalBudget, String description, Member manager, Set<Member> members) {
		project.setName(name);
		project.setStartDate(startDate);
		project.setExpectedEndDate(expectedEndDate);
		project.setActualEndDate(actualEndDate);
		project.setTotalBudget(totalBudget);
		project.setDescription(description);
		project.setManager(manager);
		project.setMembers(members);
	}

}

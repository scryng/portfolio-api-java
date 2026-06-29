package com.scryng.portfolio.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scryng.portfolio.domain.enums.ProjectStatus;
import com.scryng.portfolio.dto.request.CreateProjectRequest;
import com.scryng.portfolio.dto.request.UpdateProjectRequest;
import com.scryng.portfolio.dto.request.UpdateProjectStatusRequest;
import com.scryng.portfolio.dto.response.ProjectResponse;
import com.scryng.portfolio.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
		return projectService.create(request);
	}

	@GetMapping("/{id}")
	public ProjectResponse findById(@PathVariable Long id) {
		return projectService.findById(id);
	}

	@GetMapping
	public Page<ProjectResponse> findAll(
			@RequestParam(required = false) ProjectStatus status,
			@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
		return projectService.findAll(status, pageable);
	}

	@PutMapping("/{id}")
	public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProjectRequest request) {
		return projectService.update(id, request);
	}

	@PatchMapping("/{id}/status")
	public ProjectResponse updateStatus(@PathVariable Long id,
			@Valid @RequestBody UpdateProjectStatusRequest request) {
		return projectService.updateStatus(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		projectService.delete(id);
	}

}

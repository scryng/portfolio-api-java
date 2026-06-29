package com.scryng.portfolio.mock;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.scryng.portfolio.dto.request.ExternalMemberRequest;
import com.scryng.portfolio.dto.response.ExternalMemberResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/external/members")
@RequiredArgsConstructor
public class MockExternalMemberController {

	private final MockExternalMemberStore store;

	@PostMapping
	public ExternalMemberResponse create(@Valid @RequestBody ExternalMemberRequest request) {
		return store.create(request);
	}

	@GetMapping("/{id}")
	public ExternalMemberResponse findById(@PathVariable Long id) {
		return store.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "External member not found"));
	}

}

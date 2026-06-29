package com.scryng.portfolio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scryng.portfolio.client.MemberApiClient;
import com.scryng.portfolio.domain.entity.Member;
import com.scryng.portfolio.domain.enums.MemberRole;
import com.scryng.portfolio.domain.exception.ResourceNotFoundException;
import com.scryng.portfolio.dto.response.ExternalMemberResponse;
import com.scryng.portfolio.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberIntegrationServiceTest {

	@Mock
	private MemberApiClient memberApiClient;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private MemberIntegrationService memberIntegrationService;

	@Test
	void shouldReturnExistingMember() {
		Member member = new Member();
		member.setId(1L);
		member.setExternalId(10L);
		when(memberRepository.findByExternalId(10L)).thenReturn(Optional.of(member));

		assertThat(memberIntegrationService.syncMember(10L).getId()).isEqualTo(1L);
	}

	@Test
	void shouldCreateMemberFromExternalApi() {
		when(memberRepository.findByExternalId(10L)).thenReturn(Optional.empty());
		when(memberApiClient.findById(10L)).thenReturn(new ExternalMemberResponse(10L, "Alice", MemberRole.EMPLOYEE));
		when(memberRepository.save(org.mockito.ArgumentMatchers.any(Member.class))).thenAnswer(invocation -> {
			Member saved = invocation.getArgument(0);
			saved.setId(1L);
			return saved;
		});

		Member result = memberIntegrationService.syncMember(10L);

		assertThat(result.getName()).isEqualTo("Alice");
		verify(memberRepository).save(org.mockito.ArgumentMatchers.any(Member.class));
	}

	@Test
	void shouldThrowWhenMemberNotFoundById() {
		when(memberRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> memberIntegrationService.findById(99L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

}

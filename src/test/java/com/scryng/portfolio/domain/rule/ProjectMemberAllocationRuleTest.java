package com.scryng.portfolio.domain.rule;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.scryng.portfolio.domain.entity.Member;
import com.scryng.portfolio.domain.enums.MemberRole;
import com.scryng.portfolio.domain.exception.MemberAllocationException;

class ProjectMemberAllocationRuleTest {

	@Test
	void shouldRejectTeamBelowMinimum() {
		assertThatThrownBy(() -> ProjectMemberAllocationRule.validateTeamSize(Set.of()))
				.isInstanceOf(MemberAllocationException.class);
	}

	@Test
	void shouldRejectNonEmployeeMember() {
		Member manager = member(MemberRole.MANAGER);

		assertThatThrownBy(() -> ProjectMemberAllocationRule.validateMemberRoles(Set.of(manager)))
				.isInstanceOf(MemberAllocationException.class);
	}

	@Test
	void shouldRejectMemberWithTooManyActiveProjects() {
		Member employee = member(MemberRole.EMPLOYEE);

		assertThatThrownBy(() -> ProjectMemberAllocationRule.validateActiveProjectCount(employee, 3))
				.isInstanceOf(MemberAllocationException.class);
	}

	@Test
	void shouldAllowValidEmployeeAllocation() {
		Member employee = member(MemberRole.EMPLOYEE);

		assertThatCode(() -> {
			ProjectMemberAllocationRule.validateTeamSize(Set.of(employee));
			ProjectMemberAllocationRule.validateMemberRoles(Set.of(employee));
			ProjectMemberAllocationRule.validateActiveProjectCount(employee, 2);
		}).doesNotThrowAnyException();
	}

	private Member member(MemberRole role) {
		Member member = new Member();
		member.setName("Member");
		member.setRole(role);
		member.setExternalId(1L);
		return member;
	}

}

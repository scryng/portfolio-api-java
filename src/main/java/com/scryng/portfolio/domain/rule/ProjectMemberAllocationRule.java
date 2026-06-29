package com.scryng.portfolio.domain.rule;

import java.util.Set;

import com.scryng.portfolio.domain.entity.Member;
import com.scryng.portfolio.domain.enums.MemberRole;
import com.scryng.portfolio.domain.exception.MemberAllocationException;

public final class ProjectMemberAllocationRule {

	public static final int MIN_MEMBERS = 1;
	public static final int MAX_MEMBERS = 10;
	public static final int MAX_ACTIVE_PROJECTS_PER_MEMBER = 3;

	private ProjectMemberAllocationRule() {
	}

	public static void validateTeamSize(Set<Member> members) {
		int size = members.size();
		if (size < MIN_MEMBERS || size > MAX_MEMBERS) {
			throw new MemberAllocationException(
					"Project must have between " + MIN_MEMBERS + " and " + MAX_MEMBERS + " members");
		}
	}

	public static void validateMemberRoles(Set<Member> members) {
		for (Member member : members) {
			if (member.getRole() != MemberRole.EMPLOYEE) {
				throw new MemberAllocationException(
						"Only members with role EMPLOYEE can be allocated to projects");
			}
		}
	}

	public static void validateActiveProjectCount(Member member, long activeProjectCount) {
		if (activeProjectCount >= MAX_ACTIVE_PROJECTS_PER_MEMBER) {
			throw new MemberAllocationException(
					"Member " + member.getName() + " is already allocated to "
							+ MAX_ACTIVE_PROJECTS_PER_MEMBER + " active projects");
		}
	}

}

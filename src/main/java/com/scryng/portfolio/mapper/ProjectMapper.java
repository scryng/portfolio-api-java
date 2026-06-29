package com.scryng.portfolio.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.scryng.portfolio.domain.entity.Member;
import com.scryng.portfolio.domain.entity.Project;
import com.scryng.portfolio.domain.rule.RiskLevelRule;
import com.scryng.portfolio.dto.response.MemberSummaryResponse;
import com.scryng.portfolio.dto.response.ProjectResponse;

@Mapper
public interface ProjectMapper {

	@Mapping(target = "riskLevel", expression = "java(calculateRisk(project))")
	ProjectResponse toResponse(Project project);

	MemberSummaryResponse toMemberSummary(Member member);

	Set<MemberSummaryResponse> toMemberSummaries(Set<Member> members);

	default com.scryng.portfolio.domain.enums.RiskLevel calculateRisk(Project project) {
		return RiskLevelRule.calculate(
				project.getTotalBudget(), project.getStartDate(), project.getExpectedEndDate());
	}

}

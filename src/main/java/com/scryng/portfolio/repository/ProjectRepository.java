package com.scryng.portfolio.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scryng.portfolio.domain.entity.Project;
import com.scryng.portfolio.domain.enums.ProjectStatus;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

	@Query("""
			SELECT COUNT(p) FROM Project p JOIN p.members m
			WHERE m.id = :memberId AND p.status NOT IN ('CLOSED', 'CANCELLED')
			""")
	long countActiveProjectsByMemberId(@Param("memberId") Long memberId);

	@Query("""
			SELECT COUNT(p) FROM Project p JOIN p.members m
			WHERE m.id = :memberId AND p.id <> :projectId
			AND p.status NOT IN ('CLOSED', 'CANCELLED')
			""")
	long countActiveProjectsByMemberIdExcludingProject(
			@Param("memberId") Long memberId, @Param("projectId") Long projectId);

	@Query("""
			SELECT p.status AS status, COUNT(p) AS projectCount, COALESCE(SUM(p.totalBudget), 0) AS totalBudget
			FROM Project p GROUP BY p.status
			""")
	List<StatusSummaryProjection> summarizeByStatus();

	@Query(value = """
			SELECT AVG(actual_end_date - start_date)
			FROM projects
			WHERE status = 'CLOSED' AND actual_end_date IS NOT NULL
			""", nativeQuery = true)
	Double averageClosedProjectDurationDays();

	interface StatusSummaryProjection {
		ProjectStatus getStatus();

		long getProjectCount();

		BigDecimal getTotalBudget();
	}

}

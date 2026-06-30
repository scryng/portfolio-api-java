package com.scryng.portfolio.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.scryng.portfolio.domain.enums.ProjectStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "expected_end_date", nullable = false)
	private LocalDate expectedEndDate;

	@Column(name = "actual_end_date")
	private LocalDate actualEndDate;

	@Column(name = "total_budget", nullable = false, precision = 19, scale = 2)
	private BigDecimal totalBudget;

	@Column(columnDefinition = "TEXT")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "manager_id", nullable = false)
	private Member manager;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "project_members",
			joinColumns = @JoinColumn(name = "project_id"),
			inverseJoinColumns = @JoinColumn(name = "member_id"))
	private Set<Member> members = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ProjectStatus status;

}

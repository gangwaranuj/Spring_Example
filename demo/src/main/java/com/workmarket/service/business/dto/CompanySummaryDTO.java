package com.workmarket.service.business.dto;

public class CompanySummaryDTO {
	
	private final Long companyId;
	private final Integer totalPaidAssignments;
	private final Integer totalLatePaidAssignments;
	private final Integer totalCancelledAssignments;
	private final Integer totalCreatedAssignments;
	private final Integer totalCreatedGroups;
	private final Integer totalCreatedAssessments;
	private final boolean buyer;

	public CompanySummaryDTO(
		final Long companyId,
		final Integer totalPaidAssignments,
		final Integer totalLatePaidAssignments,
		final Integer totalCancelledAssignments,
		final Integer totalCreatedAssignments,
		final Integer totalCreatedGroups,
		final Integer totalCreatedAssessments,
		final boolean buyer) {

		this.companyId = companyId;
		this.totalPaidAssignments = totalPaidAssignments;
		this.totalLatePaidAssignments = totalLatePaidAssignments;
		this.totalCancelledAssignments = totalCancelledAssignments;
		this.totalCreatedAssignments = totalCreatedAssignments;
		this.totalCreatedGroups = totalCreatedGroups;
		this.totalCreatedAssessments = totalCreatedAssessments;
		this.buyer = buyer;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public Integer getTotalPaidAssignments() {
		return totalPaidAssignments;
	}

	public Integer getTotalLatePaidAssignments() {
		return totalLatePaidAssignments;
	}

	public Integer getTotalCancelledAssignments() {
		return totalCancelledAssignments;
	}

	public Integer getTotalCreatedAssignments() {
		return totalCreatedAssignments;
	}

	public Integer getTotalCreatedGroups() {
		return totalCreatedGroups;
	}

	public Integer getTotalCreatedAssessments() {
		return totalCreatedAssessments;
	}

	public boolean isBuyer() {
		return buyer;
	}
}

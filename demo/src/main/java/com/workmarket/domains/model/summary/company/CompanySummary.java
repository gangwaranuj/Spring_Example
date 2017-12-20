package com.workmarket.domains.model.summary.company;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.NumberUtilities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Entity(name = "companySummary")
@Table(name = "company_summary")
@AuditChanges
public class CompanySummary extends AuditedEntity {

	private Company company;
	private Integer totalPaidAssignments = 0;
	private Integer totalLatePaidAssignments = 0;
	private Integer totalCancelledAssignments = 0;
	private Integer totalCreatedAssignments = 0;
	private Integer totalCreatedGroups = 0;
	private Integer totalCreatedAssessments = 0;
	private boolean buyer;

	public CompanySummary() {
	}

	public CompanySummary(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "total_late_paid_assignments", nullable = false)
	public Integer getTotalLatePaidAssignments() {
		return totalLatePaidAssignments;
	}

	public void setTotalLatePaidAssignments(Integer totalLatePaidAssignments) {
		this.totalLatePaidAssignments = totalLatePaidAssignments;
	}

	@Column(name = "total_paid_assignments", nullable = false)
	public Integer getTotalPaidAssignments() {
		return totalPaidAssignments;
	}

	public void setTotalPaidAssignments(Integer totalPaidAssignments) {
		this.totalPaidAssignments = totalPaidAssignments;
	}

	@Column(name = "total_cancelled_assignments", nullable = false)
	public Integer getTotalCancelledAssignments() {
		return totalCancelledAssignments;
	}

	public void setTotalCancelledAssignments(Integer totalCancelledAssignments) {
		this.totalCancelledAssignments = totalCancelledAssignments;
	}

	@Column(name = "total_created_assignments", nullable = false)
	public Integer getTotalCreatedAssignments() {
		return totalCreatedAssignments;
	}

	public void setTotalCreatedAssignments(Integer totalCreatedAssignments) {
		this.totalCreatedAssignments = totalCreatedAssignments;
	}

	@Column(name = "total_created_groups", nullable = false)
	public Integer getTotalCreatedGroups() {
		return totalCreatedGroups;
	}

	public void setTotalCreatedGroups(Integer totalCreatedGroups) {
		this.totalCreatedGroups = totalCreatedGroups;
	}

	@Column(name = "buyer", nullable = false)
	public boolean isBuyer() {
		return buyer;
	}

	public void setBuyer(boolean buyer) {
		this.buyer = buyer;
	}


	@Column(name = "total_created_assessments", nullable = false)
	public Integer getTotalCreatedAssessments() {
		return totalCreatedAssessments;
	}

	public void setTotalCreatedAssessments(Integer totalCreatedAssessments) {
		this.totalCreatedAssessments = totalCreatedAssessments;
	}

	@Transient
	public BigDecimal calculateOnTimePaymentPercentage() {
		if (totalPaidAssignments > 0 && totalLatePaidAssignments > 0) {
			return NumberUtilities.percentageComplement(totalPaidAssignments, totalLatePaidAssignments);
		}
		return BigDecimal.ZERO;
	}
}

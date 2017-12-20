package com.workmarket.domains.compliance.model;

import com.workmarket.domains.compliance.service.CompliantVisitor;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "assignment_count_compliance_rule")
@AuditChanges
public class AssignmentCountComplianceRule extends PeriodicComplianceRule {
	private Long maxAssignments;

	@Column(name = "max_assignments")
	public Long getMaxAssignments() { return maxAssignments; }

	public void setMaxAssignments(Long maxAssignments) { this.maxAssignments = maxAssignments; }

	@Override
	@Transient
	public String getViewLabel() {
		return String.format("%d (%s)", maxAssignments, getPeriodType().getColumn());
	}

	@Transient
	public void accept(CompliantVisitor compliantVisitor, WorkComplianceCriterion complianceCriterion) {
		compliantVisitor.visit(complianceCriterion, this);
	}

	@Transient
	public void accept(CompliantVisitor compliantVisitor, WorkBundleComplianceCriterion complianceCriterion) {
		compliantVisitor.visit(complianceCriterion, this);
	}

	// This static method is used by ComplianceRuleTypeDAO
	@Transient
	public static String getHumanTypeName() { return "Maximum Assignments"; }
}

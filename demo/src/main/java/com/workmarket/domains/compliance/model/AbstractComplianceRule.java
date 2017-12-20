package com.workmarket.domains.compliance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.workmarket.domains.compliance.service.CompliantVisitor;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "compliance_rule")
@AuditChanges
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "$type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = AssignmentCountComplianceRule.class, name = "AssignmentCountComplianceRule")
})
public abstract class AbstractComplianceRule extends AuditedEntity {
	@JsonIgnore
	private ComplianceRuleSet complianceRuleSet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compliance_rule_set_id", updatable = false)
	public ComplianceRuleSet getComplianceRuleSet() {
		return complianceRuleSet;
	}

	public void setComplianceRuleSet(ComplianceRuleSet complianceRuleSet) {
		this.complianceRuleSet = complianceRuleSet;
	}

	@Transient
	public abstract String getViewLabel();

	@Transient
	public abstract void accept(CompliantVisitor compliantVisitor, WorkComplianceCriterion complianceCriterion);

	@Transient
	public abstract void accept(CompliantVisitor compliantVisitor, WorkBundleComplianceCriterion complianceCriterion);
}

package com.workmarket.domains.compliance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "compliance_rule_set")
@AuditChanges
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "$type")
public class ComplianceRuleSet extends AuditedEntity {
	String name;
	private boolean active;
	private List<AbstractComplianceRule> complianceRules;
	@JsonIgnore
	private Company company;

	@NotBlank
	@Column(name = "name")
	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	@Column(name = "active")
	public boolean isActive() { return active; }

	public void setActive(boolean active) { this.active = active; }

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "compliance_rule_set_id")
	public List<AbstractComplianceRule> getComplianceRules() {
		return complianceRules;
	}

	public void setComplianceRules(List<AbstractComplianceRule> complianceRules) { this.complianceRules = complianceRules; }

	@ManyToOne
	@JoinColumn(name = "company_id", updatable = false)
	public Company getCompany() { return company; }

	public void setCompany(Company company) { this.company = company; }
}

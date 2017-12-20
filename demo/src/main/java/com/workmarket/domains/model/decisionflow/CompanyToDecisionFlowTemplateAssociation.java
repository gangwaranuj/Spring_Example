package com.workmarket.domains.model.decisionflow;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name = "companyToDecisionTemplateAssociation")
@Table(name = "company_to_decision_flow_template_association")
@AuditChanges
public class CompanyToDecisionFlowTemplateAssociation extends DeletableEntity {
	private Company company;
	private String decisionFlowTemplateUuid;

	public CompanyToDecisionFlowTemplateAssociation() {
		super();
	}

	public CompanyToDecisionFlowTemplateAssociation(Company company, String decisionFlowTemplateUuid) {
		super();
		this.company = company;
		this.decisionFlowTemplateUuid = decisionFlowTemplateUuid;
	}

	@OneToOne
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "decision_flow_template_uuid", nullable = false, length = 36)
	public String getDecisionFlowTemplateUuid() {
		return decisionFlowTemplateUuid;
	}

	public void setDecisionFlowTemplateUuid(String decisionFlowTemplateUuid) {
		this.decisionFlowTemplateUuid = decisionFlowTemplateUuid;
	}
}
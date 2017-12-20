package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * User: iloveopt
 * Date: 8/28/14
 */
@Entity(name = "companySignUpInfo")
@Table(name = "company_sign_up_info")
@AuditChanges
public class CompanySignUpInfo extends AuditedEntity {

	private static final long serialVersionUID = 6029779139643801495L;

	private Long companyId;
	private String pricingPlan;

	public CompanySignUpInfo(Long companyId, String pricingPlan) {
		this.companyId = companyId;
		this.pricingPlan = pricingPlan;
	}

	@Column(name="company_id")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name="pricing_plan")
	public String getPricingPlan() {
		return pricingPlan;
	}

	public void setPricingPlan(String pricingPlan) {
		this.pricingPlan = pricingPlan;
	}

}

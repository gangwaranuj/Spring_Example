package com.workmarket.domains.model.comment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "companyComment")
@DiscriminatorValue("CC")
@AuditChanges
public class CompanyComment extends Comment {
	private static final long serialVersionUID = 1L;

	@NotNull
	private Company company;

	@ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
}

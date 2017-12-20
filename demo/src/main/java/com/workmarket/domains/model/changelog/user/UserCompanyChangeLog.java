package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("UC")
@AuditChanges
public abstract class UserCompanyChangeLog extends UserChangeLog {
	private static final long serialVersionUID = 1L;

	@NotNull
	private Company company;

	public UserCompanyChangeLog() {}

	public UserCompanyChangeLog(Company company) {
		this.company = company;
	}

	public UserCompanyChangeLog(Long user, Long actor, Long masqueradeActor, Company company) {
		super(user, actor, masqueradeActor);
		this.company = company;
	}

	@ManyToOne(cascade = {}, optional = false)
	@JoinColumn(name = "company_id", nullable = false, unique = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	@Override
	public String getDescription() {
		return "Company changed";
	}
}

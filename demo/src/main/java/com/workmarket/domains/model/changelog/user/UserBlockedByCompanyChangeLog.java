package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("UBBC")
@AuditChanges
public class UserBlockedByCompanyChangeLog extends UserChangeLog {
	private static final long serialVersionUID = 1L;

	@NotNull
	public Company blockingCompany;

	public UserBlockedByCompanyChangeLog() {
	}

	public UserBlockedByCompanyChangeLog(Company blockingCompany) {
		this.blockingCompany = blockingCompany;
	}

	public UserBlockedByCompanyChangeLog(Long user, Long actor, Long masqueradeActor, Company blockingCompany) {
		super(user, actor, masqueradeActor);
		this.blockingCompany = blockingCompany;
	}

	@ManyToOne(cascade = {}, optional = false)
	@JoinColumn(name = "company_id", nullable = false, unique = false)
	public Company getBlockingCompany() {
		return blockingCompany;
	}

	public void setBlockingCompany(Company blockingCompany) {
		this.blockingCompany = blockingCompany;
	}

	@Transient
	@Override
	public String getDescription() {
		return "Blocked by company";
	}
}

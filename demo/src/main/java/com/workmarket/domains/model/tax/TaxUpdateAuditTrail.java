package com.workmarket.domains.model.tax;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;

@Entity(name = "taxUpdateAuditTrail")
@Table(name = "tax_update_audit")
@AuditChanges
public class TaxUpdateAuditTrail extends AuditedEntity {
	private Company company;
	private AccountServiceType serviceType = new AccountServiceType(AccountServiceType.NONE);
	private Calendar startDate;
	private Calendar endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "service_type", referencedColumnName = "code")
	public AccountServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(AccountServiceType serviceType) {
		this.serviceType = serviceType;
	}

	@Column(name = "start_date", nullable = false, updatable = false)
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "end_date", nullable = false, updatable = false)
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
}

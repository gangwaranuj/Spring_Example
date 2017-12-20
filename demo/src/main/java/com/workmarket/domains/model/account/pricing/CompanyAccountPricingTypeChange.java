package com.workmarket.domains.model.account.pricing;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.util.Calendar;

/**
 * Created by sgomez on 11/6/12 4:17 PM
 */
@Entity(name = "companyAccountPricingTypeChange")
@Table(name = "scheduled_company_account_pricing_type_change")
@AuditChanges
public class CompanyAccountPricingTypeChange extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private Company company;
	private AccountPricingType fromAccountPricingType;
	private AccountPricingType toAccountPricingType;
	private Calendar scheduledChangeDate;
	private Calendar actualChangeDate;

	@Fetch(FetchMode.JOIN)
	@OneToOne(optional = false)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Fetch(FetchMode.JOIN)
	@OneToOne(optional = false)
	@JoinColumn(name = "from_account_pricing_type_code", referencedColumnName = "code", updatable = false)
	public AccountPricingType getFromAccountPricingType() {
		return fromAccountPricingType;
	}

	public void setFromAccountPricingType(AccountPricingType fromAccountPricingType) {
		this.fromAccountPricingType = fromAccountPricingType;
	}

	@Fetch(FetchMode.JOIN)
	@OneToOne(optional = false)
	@JoinColumn(name = "to_account_pricing_type_code", referencedColumnName = "code", updatable = false)
	public AccountPricingType getToAccountPricingType() {
		return toAccountPricingType;
	}

	public void setToAccountPricingType(AccountPricingType toAccountPricingType) {
		this.toAccountPricingType = toAccountPricingType;
	}

	@Column(name = "actual_change_date")
	public Calendar getActualChangeDate() {
		return actualChangeDate;
	}

	public void setActualChangeDate(Calendar actualChangeDate) {
		this.actualChangeDate = actualChangeDate;
	}

	@Column(name = "scheduled_change_date")
	public Calendar getScheduledChangeDate() {
		return scheduledChangeDate;
	}

	public void setScheduledChangeDate(Calendar scheduledChangeDate) {
		this.scheduledChangeDate = scheduledChangeDate;
	}

	@Transient
	public boolean isFromSubscriptionToTransactional() {
		return getToAccountPricingType().isTransactionalPricing() && getFromAccountPricingType().isSubscriptionPricing();
	}

	@Transient
	public boolean isFromTransactionalToSubscription() {
		return getFromAccountPricingType().isTransactionalPricing() && getToAccountPricingType().isSubscriptionPricing();
	}

}

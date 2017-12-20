package com.workmarket.domains.model.account.payment;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

/**
 * Created by nick on 9/17/12 5:46 PM
 */
@Entity(name = "paymentTermsDurationCompanyAssociation")
@Table(name = "payment_terms_duration_company_association")
@AuditChanges
public class PaymentTermsDurationCompanyAssociation extends DeletableEntity {

	private Company company;
	private PaymentTermsDuration paymentTermsDuration;

	public PaymentTermsDurationCompanyAssociation() {
		super();
	}

	public PaymentTermsDurationCompanyAssociation(Company company, PaymentTermsDuration paymentTermsDuration) {
		super();
		this.company = company;
		this.paymentTermsDuration = paymentTermsDuration;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "payment_terms_duration_id")
	public PaymentTermsDuration getPaymentTermsDuration() {
		return paymentTermsDuration;
	}

	public void setPaymentTermsDuration(PaymentTermsDuration paymentTermsDuration) {
		this.paymentTermsDuration = paymentTermsDuration;
	}
}

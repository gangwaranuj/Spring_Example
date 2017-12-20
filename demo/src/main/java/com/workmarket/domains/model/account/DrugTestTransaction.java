package com.workmarket.domains.model.account;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity(name = "drug_test_transaction")
@Table(name = "drug_test_transaction")
@AuditChanges
public class DrugTestTransaction extends RegisterTransaction {
	private static final long serialVersionUID = 1L;
	private Boolean paidOnCreditCard;

	@Column(name = "paid_on_credit_card", nullable = false)
	public Boolean getPaidOnCreditCard() {
		return paidOnCreditCard;
	}

	public void setPaidOnCreditCard(Boolean paidOnCreditCard) {
		this.paidOnCreditCard = paidOnCreditCard;
	}


}

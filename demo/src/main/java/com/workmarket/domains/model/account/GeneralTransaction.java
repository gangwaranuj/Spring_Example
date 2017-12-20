package com.workmarket.domains.model.account;


import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

@Entity(name = "general_transaction")
@Table(name = "general_transaction")
@AuditChanges
public class GeneralTransaction extends RegisterTransaction{

	private static final long serialVersionUID = 1L;
	private RegisterTransaction parentTransaction;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_transaction_id", referencedColumnName = "id")
	public RegisterTransaction getParentTransaction() {
		return parentTransaction;
	}
	public void setParentTransaction(RegisterTransaction parentTransaction) {
		this.parentTransaction = parentTransaction;
	}
}

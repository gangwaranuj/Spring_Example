package com.workmarket.domains.model.account;

import javax.persistence.Entity;

@Entity(name = "transactionStatus")
public class TransactionStatus extends BankAccountTransactionStatus {

	private static final long serialVersionUID = 1L;

	public TransactionStatus() {
		super();
	}

	public TransactionStatus(String code) {
		super(code);
	}
}

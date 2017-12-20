package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class BankAccountPagination extends AbstractPagination<AbstractBankAccount> implements Pagination<AbstractBankAccount> {
	public BankAccountPagination() {}
	public BankAccountPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		ACTIVE,
		CONFIRMED,
		TYPE,
		COUNTRY
	}
	public enum SORTS {
		TYPE,
		ACCOUNT_NAME
	}
}
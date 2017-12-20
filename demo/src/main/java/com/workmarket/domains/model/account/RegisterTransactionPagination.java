package com.workmarket.domains.model.account;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class RegisterTransactionPagination extends AbstractPagination<RegisterTransaction> implements Pagination<RegisterTransaction> {

	public enum FILTER_KEYS {
		SINCE, BEFORE
	}

	public RegisterTransactionPagination() {
		super(false);
	}

	public RegisterTransactionPagination(boolean returnAllRows) {
		super(returnAllRows);
	}


}

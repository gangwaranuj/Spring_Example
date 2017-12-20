package com.workmarket.domains.model.contract;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ContractPagination extends AbstractPagination<Contract> implements Pagination<Contract> {
	public ContractPagination() {}
	public ContractPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		ACTIVE
	}

	public enum SORTS {
		NAME,
		MODIFICATION_DATE,
		CREATOR
	}
}

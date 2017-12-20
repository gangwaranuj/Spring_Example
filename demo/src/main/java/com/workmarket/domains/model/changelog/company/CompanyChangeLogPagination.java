package com.workmarket.domains.model.changelog.company;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;


public class CompanyChangeLogPagination extends AbstractPagination<CompanyChangeLog> implements Pagination<CompanyChangeLog> {

	public CompanyChangeLogPagination() {
	}

	public CompanyChangeLogPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		MASQUERADED
	}

	public enum SORTS {
		ID,
		TYPE,
		COMPANY_NAME,
		CREATED_ON,
		ACTOR_LAST_NAME,
		MASQUERADE_ACTOR_LAST_NAME,
		OLD_VALUE,
		NEW_VALUE;
	}
}

package com.workmarket.domains.model.crm;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ClientCompanyPagination extends AbstractPagination<ClientCompany> implements Pagination<ClientCompany> {

	public ClientCompanyPagination() {}
	public ClientCompanyPagination (boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {}
	
	public enum SORTS {
		NAME, CUSTOMER_ID
	}
}

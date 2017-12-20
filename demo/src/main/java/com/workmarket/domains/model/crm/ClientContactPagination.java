package com.workmarket.domains.model.crm;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ClientContactPagination extends AbstractPagination<ClientContact> implements Pagination<ClientContact> {

	private boolean withAssociations = true;

	public ClientContactPagination() {}
	public ClientContactPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		PRIMARY_CONTACT_FLAG, 
		CLIENT_COMPANY_ID, 
		CLIENT_LOCATION_NAME, 
		CLIENT_COMPANY_NAME, 
		CLIENT_CONTACT_NAME, 
		CLIENT_LOCATION_ID, 
		CLIENT_LOCATION_ADDRESS, 
		COMPANY_ID
	}
	public enum SORTS {
		CLIENT_COMPANY_NAME, 
		CLIENT_CONTACT_NAME, 
		CLIENT_LOCATION_NAME,
		CLIENT_CONTACT_TITLE,
		CLIENT_CONTACT_EMAIL
	}

	public boolean isWithAssociations() {
		return withAssociations;
	}

	public void setWithAssociations(boolean withAssociations) {
		this.withAssociations = withAssociations;
	}
}

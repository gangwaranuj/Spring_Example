package com.workmarket.domains.model.crm;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ClientLocationPagination extends AbstractPagination<ClientLocation> implements Pagination<ClientLocation> {

	public ClientLocationPagination() {}
	public ClientLocationPagination(boolean returnAllRow) {
		super(returnAllRow);
	}

	public enum FILTER_KEYS {
		LOCATION_NAME, CLIENT_NAME, CLIENT_ID, CONTACT_NAME
	}
	public enum SORTS {
		LOCATION_NAME, LOCATION_ID, LOCATION_TYPE
	}
}

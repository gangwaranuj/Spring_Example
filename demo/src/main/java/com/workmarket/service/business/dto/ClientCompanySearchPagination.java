package com.workmarket.service.business.dto;

import com.workmarket.domains.model.AbstractPagination;

public class ClientCompanySearchPagination extends AbstractPagination<ClientCompanySearchDTO> {

	public enum FILTER_KEYS {
		PRIMARY_CONTACT_FLAG,
		CLIENT_COMPANY_ID,
		CLIENT_LOCATION_NAME,
		CLIENT_COMPANY_NAME,
		CLIENT_CONTACT_NAME,
		CLIENT_LOCATION_ID,
		CLIENT_LOCATION_ADDRESS
	}

	public enum SORTS {
		CLIENT_COMPANY_NAME,
		CLIENT_CONTACT_NAME,
		CLIENT_LOCATION_NAME
	}
}

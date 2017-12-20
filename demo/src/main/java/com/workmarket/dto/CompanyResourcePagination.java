package com.workmarket.dto;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class CompanyResourcePagination extends AbstractPagination<CompanyResource> implements Pagination<CompanyResource> {

    public CompanyResourcePagination() {
    }

    public CompanyResourcePagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS {
    	KEYWORDS,
    	RESOURCE_TYPE; //See ResourceType
	}

	public enum SORTS {
		RESOURCE_LASTNAME,
		RESOURCE_COMPANY_NAME,
		LAST_LOGIN,
		YTD_WORK,
		YTD_PAYMENTS,
		ROLES,
		LANE,
		USER_NUMBER,
		CITY,
		STATE,
		POSTAL_CODE;
	}

	public enum ResourceType {
		EMPLOYEE,
		LANE_1,
		VENDOR,
		THIRD_PARTY;
	}
}

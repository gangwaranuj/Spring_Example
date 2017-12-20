package com.workmarket.domains.work.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WorkPagination extends AbstractPagination<Work> implements Pagination<Work> {

    public WorkPagination() {}

    public WorkPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

	public enum FILTER_KEYS {
		WORK_STATUS, WORK_RESOURCE_STATUS, BUYER_USER_ID, WORK_RESOURCE_USER_ID,
        INCLUDE_BOTH_USERS, KEYWORD, FROM_DATE, THROUGH_DATE, COMPANY_ID, PAYMENT_DATE
		
	}

    public enum SORTS {
		WORK_ID, TITLE, SCHEDULE_FROM, SCHEDULE_THROUGH, PRICE_FEE, CLIENT_COMPANY_NAME, CITY, RESOURCE_NAME, WORK_STATUS, CREATED_DATE, COMPANY_ID
	}
}

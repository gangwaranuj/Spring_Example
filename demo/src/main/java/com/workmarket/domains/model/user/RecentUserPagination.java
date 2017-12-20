package com.workmarket.domains.model.user;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class RecentUserPagination extends AbstractPagination<RecentUser> implements Pagination<RecentUser> {

	public RecentUserPagination() {}
	public RecentUserPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		LAST_30_DAYS,
		LAST_60_DAYS,
		LAST_90_DAYS,
		LAST_120_DAYS,
		LAST_365_DAYS,
		YTD, 
		USER_NAME
	}
	
	public enum SORTS {
		LAST_NAME, FIRST_NAME, COMPANY_NAME, REGISTRATION_DATE, EMAIL, WORK_PHONE
	}
}

package com.workmarket.domains.model.user;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class CompanyUserPagination extends AbstractPagination<CompanyUser> implements Pagination<CompanyUser> {

	public CompanyUserPagination() {}
	public CompanyUserPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		IS_INACTIVE
	}
	
	public enum SORTS {
		CREATED_ON, USER_NUMBER, FIRST_NAME, LAST_NAME, EMAIL, ROLES_STRING, LANE_ACCESS_STRING, LATEST_ACTIVITY
	}
}

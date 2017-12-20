package com.workmarket.domains.model;

public class UserPagination extends AbstractPagination<User> implements Pagination<User> {

	public UserPagination() {}

	public UserPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		//For ClientSvcService
		TYPE_EXPERIENCE,
		CREATION_DATE_FROM,
		CREATION_DATE_TO,
		CSR_LEAD_ISOPEN,
		FIRST_NAME,
		LAST_NAME,
		COMPANY_NAME,
		KEYWORDS
	}

	public enum SORTS {
		USER_ID, USER_NUMBER, USER_NAME, FIRST_NAME, LAST_NAME, USER_EMAIL, CREATION_DATE, MODIFIED_DATE, COMPANY_NAME,
		TYPE_OF_EXPERIENCE, IS_CSR_OPEN
	}
}

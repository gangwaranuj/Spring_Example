package com.workmarket.domains.model.screening;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ScreenedUserPagination extends AbstractPagination<ScreenedUser> implements Pagination<ScreenedUser> {

    public ScreenedUserPagination() {}

    public ScreenedUserPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS {}
	
	public enum SORTS {
		CREDITCHECK_STATUS, BACKGROUND_CHECK_STATUS, DRUGTEST_STATUS, USER_ID, USER_LASTNAME, USER_FIRSTNAME, COMPANY_NAME
	}
}

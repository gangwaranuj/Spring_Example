package com.workmarket.domains.model.license;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class LicensePagination extends AbstractPagination<License> implements Pagination<License> {

    public LicensePagination() {}

    public LicensePagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS { VERIFICATION_STATUS }
	public enum SORTS { 
		VERIFICATION_STATUS, CREATED_DATE, STATE, USER_FIRST_NAME, USER_LAST_NAME, LICENSE_NAME, LAST_ACTIVITY_DATE; 
	}
}

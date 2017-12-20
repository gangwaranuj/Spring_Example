package com.workmarket.domains.model.insurance;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class InsurancePagination extends AbstractPagination<Insurance> implements Pagination<Insurance> {
	
	public InsurancePagination() {
		super(false);
	}
	
	public InsurancePagination(boolean returnAllRows) {
		super(returnAllRows);
	}
	
	public enum FILTER_KEYS {
		VERIFICATION_STATUS, USER_NAME;
	}
	
	public enum SORTS {
		CREATED_DATE, USER_FIRST_NAME, USER_LAST_NAME, VERIFICATION_STATUS, INSURANCE_NAME, INDUSTRY, LAST_ACTIVITY_DATE;
	}
}

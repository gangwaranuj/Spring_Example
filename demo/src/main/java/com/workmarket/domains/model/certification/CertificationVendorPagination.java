package com.workmarket.domains.model.certification;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class CertificationVendorPagination extends AbstractPagination<CertificationVendor> implements Pagination<CertificationVendor> {
	
	public CertificationVendorPagination() {}

    public CertificationVendorPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS { VERIFICATION_STATUS }
	public enum SORTS {
		VERIFICATION_STATUS, CREATED_DATE, USER_FIRST_NAME, USER_LAST_NAME, INDUSTRY, VENDOR_NAME, LAST_ACTIVITY_DATE;	
	}
}

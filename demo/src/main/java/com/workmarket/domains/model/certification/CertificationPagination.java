package com.workmarket.domains.model.certification;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class CertificationPagination extends AbstractPagination<Certification> implements Pagination<Certification> {

    public CertificationPagination() {}

    public CertificationPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS { 
    	VERIFICATION_STATUS, VENDOR_ID;
    }
	
    public enum SORTS {
		VERIFICATION_STATUS, CREATED_DATE, USER_FIRST_NAME, USER_LAST_NAME, VENDOR_NAME, CERTIFICATION_NAME, LAST_ACTIVITY_DATE;	
	}
}

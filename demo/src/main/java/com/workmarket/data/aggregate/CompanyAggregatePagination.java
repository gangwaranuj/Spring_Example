package com.workmarket.data.aggregate;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class CompanyAggregatePagination extends AbstractPagination<CompanyAggregate> implements Pagination<CompanyAggregate> {

    public CompanyAggregatePagination() {
    }

    public CompanyAggregatePagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS {
    	COMPANY_NAME, COMPANY_ID, COMPANY_STATUS,COMPANY_TYPE;
	}
	
	public enum SORTS {
		COMPANY_NAME, LANE_0, LANE_1, LANE_2, LANE_3, YTD_ASSIGNMENTS, AVAILABLE_CREDIT_LIMIT, CREATED_ON, COMPANY_STATUS;
	}
}

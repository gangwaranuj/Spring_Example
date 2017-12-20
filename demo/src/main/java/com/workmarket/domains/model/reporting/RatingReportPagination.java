package com.workmarket.domains.model.reporting;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

/**
 * User: iloveopt
 * Date: 11/18/13
 */
public class RatingReportPagination extends AbstractPagination<RatingReport> implements Pagination<RatingReport> {

	public RatingReportPagination() {}

	public RatingReportPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	private boolean fetchAll = false;

	public boolean isFetchAll() {
		return fetchAll;
	}

	public void setFetchAll(boolean fetchAll) {
		this.fetchAll = fetchAll;
	}

	public enum FILTER_KEYS {
		REVIEW_SHARED_FLAG, RATING_SHARED_FLAG, FROM_DATE, THROUGH_DATE
	}

	public enum SORTS {
		CREATED_ON, VALUE, WORK_NUMBER, CLIENT_NAME, RESOURCE_NAME, TITLE, PAID_ON,
	}

}

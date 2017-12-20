package com.workmarket.domains.model.rating;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class RatingPagination extends AbstractPagination<Rating> implements Pagination<Rating> {
	
	public RatingPagination() {
		super(false);
	}
	
	public RatingPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
	
	public enum FILTER_KEYS {
		REVIEW_SHARED_FLAG, RATING_SHARED_FLAG
	}
	
	public enum SORTS {
		CREATED_ON, VALUE, WORK_NUMBER, CLIENT_NAME, RESOURCE_NAME, TITLE
	}
}
package com.workmarket.web.models;

import com.workmarket.domains.model.Pagination;

public interface PaginatableHttpRequest {
	Integer getStart();
	Integer getLimit();
	Integer getSortColumnIndex();
	String getSortColumn();
	Pagination.SORT_DIRECTION getSortColumnDirection();
}

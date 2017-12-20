package com.workmarket.domains.work.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WorkTemplatePagination extends AbstractPagination<WorkTemplate> implements Pagination<WorkTemplate> {

	public static final WorkTemplatePagination RETURN_ALL_ROWS = new WorkTemplatePagination(true);

	public WorkTemplatePagination() {}
	public WorkTemplatePagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {}
	public enum SORTS {
		NAME, LATEST_CREATED_WORK_DATE
	}
}

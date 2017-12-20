package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;


public class WorkChangeLogPagination extends AbstractPagination<WorkChangeLog> implements Pagination<WorkChangeLog> {

	public WorkChangeLogPagination() {
	}

	public WorkChangeLogPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		TYPE
	}

	public enum SORTS {}

}

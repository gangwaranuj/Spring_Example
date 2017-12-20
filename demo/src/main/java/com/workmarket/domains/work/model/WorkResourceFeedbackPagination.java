package com.workmarket.domains.work.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WorkResourceFeedbackPagination extends AbstractPagination<WorkResourceFeedbackRow> implements Pagination<WorkResourceFeedbackRow> {
	public enum FILTER_KEYS {
		COMPANY_SCOPE
	}
	public enum SORTS {}
}

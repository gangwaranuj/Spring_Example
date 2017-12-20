package com.workmarket.data.report.assessment;

import java.util.List;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class AttemptReportPagination extends AbstractPagination<AttemptReportRow> implements Pagination<AttemptReportRow> {
	private List<String> columnNames = Lists.newArrayList();
	
	public enum FILTER_KEYS {}
	public enum SORTS {}
	
	public List<String> getColumnNames() {
		return columnNames;
	}

	public AttemptReportPagination() {
		super(false);
	}

	public AttemptReportPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}
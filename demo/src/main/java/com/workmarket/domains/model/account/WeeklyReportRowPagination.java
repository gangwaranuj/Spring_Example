package com.workmarket.domains.model.account;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WeeklyReportRowPagination extends AbstractPagination<WeeklyReportRow> implements Pagination<WeeklyReportRow> {

	public enum FILTER_KEYS {
		;
	}

	public enum SORTS {
		COMPANY_NAME("c.effective_name");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}
}

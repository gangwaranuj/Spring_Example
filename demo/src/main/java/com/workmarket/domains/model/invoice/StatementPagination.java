package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class StatementPagination extends AbstractPagination<Statement> implements Pagination<Statement> {

	public enum FILTER_KEYS {
		DUE_DATE_FROM,
		DUE_DATE_TO,
		INVOICE_STATUS,
		COMPANY_ID;
	}

	public enum SORTS {

		DUE_DATE("dueDate");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public StatementPagination() {
		super(false);
	}

	public StatementPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}

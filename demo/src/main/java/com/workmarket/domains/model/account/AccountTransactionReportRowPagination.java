package com.workmarket.domains.model.account;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

/**
 * Created by IntelliJ IDEA.
 * User: rocio
 * Date: 3/1/12
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountTransactionReportRowPagination extends AbstractPagination<AccountTransactionReportRow> implements Pagination<AccountTransactionReportRow> {

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

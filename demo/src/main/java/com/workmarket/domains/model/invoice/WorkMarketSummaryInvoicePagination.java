package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WorkMarketSummaryInvoicePagination extends AbstractPagination<WorkMarketSummaryInvoice> implements Pagination<WorkMarketSummaryInvoice> {

	public enum FILTER_KEYS {
		CREATED_DATE_FROM,
		DUE_DATE_FROM,
		DUE_DATE_TO,
		INVOICE_STATUS,
		COMPANY_ID,
		COMPANY_NAME,
		INVOICE_NUMBER;
	}

	public enum SORTS {

		DUE_DATE("dueDate"),
		INVOICE_TYPE("type"),
		INVOICE_STATUS("invoiceStatusType.code"),
		INVOICE_NUMBER("invoiceNumber"),
		INVOICE_AMOUNT("balance"),
		PAYMENT_DATE("paymentDate"),
		COMPANY_NAME("company.effectiveName"),
		CREATED_DATE("createdOn");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public WorkMarketSummaryInvoicePagination() {
		super(false);
	}

	public WorkMarketSummaryInvoicePagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}

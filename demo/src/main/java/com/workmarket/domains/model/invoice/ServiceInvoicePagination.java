package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ServiceInvoicePagination extends AbstractPagination<AbstractServiceInvoice> implements Pagination<AbstractServiceInvoice> {

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
		INVOICE_TYPE("class"),
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

	public ServiceInvoicePagination() {
		super(false);
	}

	public ServiceInvoicePagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}

package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

import java.math.BigDecimal;

public class InvoicePagination extends AbstractPagination<Invoice> implements Pagination<Invoice> {
	
	private BigDecimal totalBalance = BigDecimal.ZERO;

	public enum FILTER_KEYS {
		DUE_DATE_FROM,
		DUE_DATE_TO,
		INVOICE_STATUS,
		COMPANY_ID;
	}

	public enum SORTS {

		DUE_DATE("dueDate"),
		INVOICE_STATUS("invoiceStatusType.code"),
		INVOICE_NUMBER("invoiceNumber"),
		INVOICE_AMOUNT("balance"),
		PAYMENT_DATE("paymentDate");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public InvoicePagination() {
		super(false);
	}

	public InvoicePagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public BigDecimal getTotalBalance() {
		if (totalBalance == null) {
			return BigDecimal.ZERO;
		}
		return totalBalance;
	}

	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}
}

package com.workmarket.data.report.work;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AccountStatementDetailPagination extends AbstractPagination<AccountStatementDetailRow> implements Pagination<AccountStatementDetailRow> {	
	
	private Map<String,BigDecimal> balanceByStatus = new HashMap<>();
	private Map<String,BigDecimal> amountEarnedByStatus = new HashMap<>();

	public AccountStatementDetailPagination() {
		super(false);
	}
	
	public AccountStatementDetailPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {}

	public enum SORTS {
		INVOICE_CREATED_ON("invoice.created_on"),
		INVOICE_DUE_DATE("invoice.due_date"), 
		INVOICE_STATUS("invoiceStatusTypeCode"),
		INVOICE_NUMBER("invoice.invoice_number"),
		INVOICE_AMOUNT("invoice.balance");

		
		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public Map<String, BigDecimal> getBalanceByStatus() {
		return balanceByStatus;
	}
	
	public void setBalanceForStatus(String status, BigDecimal balance) {
		balanceByStatus.put(status, balance);
	}
	
	public BigDecimal getBalanceForStatus(String status) {
		return (balanceByStatus.containsKey(status)) ? balanceByStatus.get(status) : BigDecimal.ZERO;
	}

	public BigDecimal getTotalBalance() {
		BigDecimal total = BigDecimal.ZERO;
		for (BigDecimal v : balanceByStatus.values())
			total = total.add(v);
		return total;
	}
	
	public Map<String, BigDecimal> getAmountEarnedByStatus() {
		return amountEarnedByStatus;
	}
	
	public void setAmountEarnedForStatus(String status, BigDecimal balance) {
		amountEarnedByStatus.put(status, balance);
	}
	
	public BigDecimal getAmountEarnedForStatus(String status) {
		return (amountEarnedByStatus.containsKey(status)) ? amountEarnedByStatus.get(status) : BigDecimal.ZERO;
	}

	public BigDecimal getTotalAmountEarned() {
		BigDecimal total = BigDecimal.ZERO;
		for (BigDecimal v : amountEarnedByStatus.values())
			total = total.add(v);
		return total;
	}
}

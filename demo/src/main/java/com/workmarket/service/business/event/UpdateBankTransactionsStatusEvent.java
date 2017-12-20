package com.workmarket.service.business.event;

import java.util.List;

public class UpdateBankTransactionsStatusEvent extends Event {

	private static final long serialVersionUID = -1330032191987369991L;

	private Long userId;
	private List<Long> transactionIds;
	private String notes;
	private String statusCode;

	public UpdateBankTransactionsStatusEvent(Long userId, List<Long> transactionIds, String notes, String statusCode) {
		this.userId = userId;
		this.transactionIds = transactionIds;
		this.notes = notes;
		this.statusCode = statusCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<Long> getTransactionIds() {
		return transactionIds;
	}

	public void setTransactionIds(List<Long> transactionIds) {
		this.transactionIds = transactionIds;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}

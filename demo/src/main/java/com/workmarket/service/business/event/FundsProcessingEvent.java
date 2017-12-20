package com.workmarket.service.business.event;


public class FundsProcessingEvent extends Event {
	private static final long serialVersionUID = 4276183468928465163L;

	private long transactionId;

	public FundsProcessingEvent(long transactionId) {
		super();
		this.transactionId = transactionId;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

}

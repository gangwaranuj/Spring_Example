package com.workmarket.service.business.dto;

import com.workmarket.domains.model.account.authorization.TransactionAuthorizationAudit;

public class PaymentResponseDTO {

	private boolean approved;
	private String responseMessage;

	private Long creditCardTransactionId;
	private Long creditCardFeeTransactionId;

	private TransactionAuthorizationAudit authorizationAudit;

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Long getCreditCardTransactionId() {
		return creditCardTransactionId;
	}

	public void setCreditCardTransactionId(Long creditCardTransactionId) {
		this.creditCardTransactionId = creditCardTransactionId;
	}

	public Long getCreditCardFeeTransactionId() {
		return creditCardFeeTransactionId;
	}

	public void setCreditCardFeeTransactionId(Long creditCardFeeTransactionId) {
		this.creditCardFeeTransactionId = creditCardFeeTransactionId;
	}

	public TransactionAuthorizationAudit getAuthorizationAudit() {
		return authorizationAudit;
	}

	public void setAuthorizationAudit(TransactionAuthorizationAudit authorizationAudit) {
		this.authorizationAudit = authorizationAudit;
	}
}

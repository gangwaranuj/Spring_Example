package com.workmarket.domains.model.account.request;

import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * Author: rocio
 */
public class FundsRequest {

	private long companyId;
	private String note;
	private BigDecimal amount;
	private String registerTransactionTypeCode;
	private boolean notify = true;

	public FundsRequest() {
	}

	public FundsRequest(long companyId, String note, BigDecimal amount, String registerTransactionTypeCode) {
		Assert.notNull(amount);
		Assert.hasText(registerTransactionTypeCode);
		this.registerTransactionTypeCode = registerTransactionTypeCode;
		this.companyId = companyId;
		this.note = note;
		this.amount = amount;
	}

	public long getCompanyId() {
		return companyId;
	}

	public FundsRequest setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	public String getNote() {
		return note;
	}

	public FundsRequest setNote(String note) {
		this.note = note;
		return this;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public FundsRequest setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public String getRegisterTransactionTypeCode() {
		return registerTransactionTypeCode;
	}

	public FundsRequest setRegisterTransactionTypeCode(String registerTransactionTypeCode) {
		this.registerTransactionTypeCode = registerTransactionTypeCode;
		return this;
	}

	public boolean isNotify() {
		return notify;
	}

	public FundsRequest setNotify(boolean notify) {
		this.notify = notify;
		return this;
	}
}

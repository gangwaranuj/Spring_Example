package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.RegisterTransactionType;

import java.math.BigDecimal;

public class TransactionBreakdown {
	private BigDecimal gross = BigDecimal.ZERO;
	private BigDecimal fee = BigDecimal.ZERO;
	private BigDecimal secretFee = BigDecimal.ZERO;
	private RegisterTransactionType transactionType;
	private RegisterTransactionType feeTransactionType;
	private RegisterTransactionType secretFeeTransactionType;

	public BigDecimal getGross() {
		return gross;
	}

	public TransactionBreakdown setGross(BigDecimal gross) {
		this.gross = gross;
		return this;
	}

	public BigDecimal getNet() {
		return gross.subtract(fee);
	}

	public BigDecimal getFee() {
		return fee;
	}

	public TransactionBreakdown setFee(BigDecimal fee) {
		this.fee = fee;
		return this;
	}

	public BigDecimal getSecretFee() {
		return secretFee;
	}

	public TransactionBreakdown setSecretFee(BigDecimal secretFee) {
		this.secretFee = secretFee;
		return this;
	}

	public RegisterTransactionType getTransactionType() {
		return transactionType;
	}

	public TransactionBreakdown setTransactionType(RegisterTransactionType transactionType) {
		this.transactionType = transactionType;
		return this;
	}

	public RegisterTransactionType getFeeTransactionType() {
		return feeTransactionType;
	}

	public TransactionBreakdown setFeeTransactionType(RegisterTransactionType feeTransactionType) {
		this.feeTransactionType = feeTransactionType;
		return this;
	}

	public RegisterTransactionType getSecretFeeTransactionType() {
		return secretFeeTransactionType;
	}

	public TransactionBreakdown setSecretFeeTransactionType(RegisterTransactionType secretFeeTransactionType) {
		this.secretFeeTransactionType = secretFeeTransactionType;
		return this;
	}

	public boolean hasFee() {
		return BigDecimal.ZERO.compareTo(fee) < 0;
	}

	public boolean hasSecretFee() {
		return BigDecimal.ZERO.compareTo(secretFee) < 0;
	}
}

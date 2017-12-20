package com.workmarket.service.infra.status;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.service.status.ResponseStatus;

import java.util.List;

public enum FastFundInvoiceStatus implements ResponseStatus {
	SUCCESS,
	FAILURE,
	INVOICE_NOT_FOUND;

	private static List<FastFundInvoiceStatus> FAILURE_STATES = ImmutableList.of(FAILURE, INVOICE_NOT_FOUND);

	public boolean isSuccessful() {
		return !FAILURE_STATES.contains(this);
	}

	public boolean isFailure() {
		return FAILURE_STATES.contains(this);
	}
}

package com.workmarket.service.infra.business.wrapper;

import com.workmarket.service.infra.status.FastFundInvoiceStatus;

import java.math.BigDecimal;
import java.util.Calendar;

public class FastFundInvoiceResponse {

	private FastFundInvoiceStatus status;
	private Calendar fastFundsOn;

	private static FastFundInvoiceResponse create(FastFundInvoiceStatus fastFundInvoiceStatus) {
		FastFundInvoiceResponse fastFundsResponse =  new FastFundInvoiceResponse();
		fastFundsResponse.setStatus(fastFundInvoiceStatus);
		return fastFundsResponse;
	}

	public static FastFundInvoiceResponse success() {
		return create(FastFundInvoiceStatus.SUCCESS);
	}

	public static FastFundInvoiceResponse fail() {
		return create(FastFundInvoiceStatus.FAILURE);
	}

	public static FastFundInvoiceResponse invoiceNotFound() {
		return create(FastFundInvoiceStatus.INVOICE_NOT_FOUND);
	}

	public FastFundInvoiceStatus getStatus() {
		return status;
	}

	public FastFundInvoiceResponse setStatus(FastFundInvoiceStatus status) {
		this.status = status;
		return this;
	}

	public Calendar getFastFundsOn() {
		return fastFundsOn;
	}

	public FastFundInvoiceResponse setFastFundsOn(Calendar fastFundsOn) {
		this.fastFundsOn = fastFundsOn;
		return this;
	}

	public boolean isSuccess() {
		return FastFundInvoiceStatus.SUCCESS.equals(this.getStatus());
	}

	public boolean isFail() {
		return FastFundInvoiceStatus.FAILURE.equals(this.getStatus());
	}
}

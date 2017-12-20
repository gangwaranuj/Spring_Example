package com.workmarket.service.business.wrapper;

import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.status.ResponseStatus;
import com.workmarket.common.service.wrapper.response.MessageResponse;

import java.util.List;

/**
 * Created by nick on 4/16/13 1:53 PM
 */
public class InvoiceResponse extends MessageResponse {

	protected String invoiceNumber;

	public InvoiceResponse(ResponseStatus status) {
		super(status);
	}

	public InvoiceResponse(ResponseStatus status, String message) {
		super(status, message);
	}

	public InvoiceResponse(ResponseStatus status, List<String> messages) {
		super(status, messages);
	}

	public static InvoiceResponse success(String invoiceNumber) {
		return new InvoiceResponse(BaseStatus.SUCCESS).setInvoiceNumber(invoiceNumber);
	}

	public static InvoiceResponse fail(String invoiceNumber) {
		return new InvoiceResponse(BaseStatus.FAILURE).setInvoiceNumber(invoiceNumber);
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public InvoiceResponse setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
		return this;
	}
}

package com.workmarket.service.business.wrapper;

import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.service.business.status.CloseWorkStatus;

public class CloseWorkResponse extends MessageResponse {

	protected String invoiceNumber;
	protected Integer paymentTermsDays;

	private CloseWorkResponse() {
		super();
	}

	public static CloseWorkResponse create() {
		return new CloseWorkResponse();
	}

	public static CloseWorkResponse create(BaseStatus status) {
		CloseWorkResponse response = new CloseWorkResponse();
		response.setStatus(status);
		return response;
	}

	public static CloseWorkResponse create(BaseStatus status, String message) {
		CloseWorkResponse response = new CloseWorkResponse();
		response.setStatus(status);
		response.addMessage(message);
		return response;
	}

	public static CloseWorkResponse success() {
		return create(CloseWorkStatus.SUCCESS);
	}

	public static CloseWorkResponse fail() {
		return create(CloseWorkStatus.FAILURE);
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public CloseWorkResponse setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
		return this;
	}

	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public CloseWorkResponse setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
		return this;
	}


}

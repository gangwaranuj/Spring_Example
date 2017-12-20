package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkInvoiceGenerateEvent extends Event {

	private static final long serialVersionUID = -7647098448145409864L;
	private final Long workId;
	private final Long invoiceId;
	private final WorkInvoiceSendType sendType;

	public WorkInvoiceGenerateEvent(final Long invoiceId, final Long workId, final WorkInvoiceSendType sendType) {
		this.invoiceId = invoiceId;
		this.workId = workId;
		this.sendType = sendType;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public Long getWorkId() {
		return workId;
	}

	public WorkInvoiceSendType getSendType() {
		return sendType;
	}

	@Override
	public String toString() {
		return "WorkInvoiceGenerateEvent{" +
				"invoiceId=" + invoiceId +
				", workId=" + workId +
				", sendType=" + sendType +
				'}';
	}
}

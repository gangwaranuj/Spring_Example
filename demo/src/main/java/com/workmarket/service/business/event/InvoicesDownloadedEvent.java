package com.workmarket.service.business.event;

import java.util.List;

public class InvoicesDownloadedEvent extends Event {
	// invoices that were downloaded
	private final List<Long> invoiceIds;
	// user requesting download, we don't set downloaded date if userId is one of Constants.ACCESS_ALL_INVOICES_USER_IDS
	private final Long loggedInUserId;

	public InvoicesDownloadedEvent(List<Long> invoiceIds, Long loggedInUserId) {
		this.invoiceIds = invoiceIds;
		this.loggedInUserId = loggedInUserId;
	}

	public List<Long> getInvoiceIds() {
		return invoiceIds;
	}

	public Long getLoggedInUserId() {
		return loggedInUserId;
	}
}

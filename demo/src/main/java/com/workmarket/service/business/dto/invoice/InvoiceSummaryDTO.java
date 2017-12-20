package com.workmarket.service.business.dto.invoice;

import java.util.List;

public class InvoiceSummaryDTO extends InvoiceDTO {

	private List<Long> invoicesIds;
	private Long invoiceSummaryId;

	public List<Long> getInvoicesIds() {
		return invoicesIds;
	}
	
	public void setInvoicesIds(List<Long> invoicesIds) {
		this.invoicesIds = invoicesIds;
	}

	public void setInvoiceSummaryId(Long invoiceSummaryId) {
		this.invoiceSummaryId = invoiceSummaryId;
	}

	public Long getInvoiceSummaryId() {
		return invoiceSummaryId;
	}
}

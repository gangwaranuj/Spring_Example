package com.workmarket.service.business.dto.invoice;

public class CreditMemoInvoiceDTO extends InvoiceDTO {

	private Long originInvoiceId;
	private String reason;
	private String note;

	public Long getOriginInvoiceId() {
		return originInvoiceId;
	}

	public void setOriginInvoiceId(Long originInvoiceId) {
		this.originInvoiceId = originInvoiceId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}

package com.workmarket.service.business.dto;

import java.math.BigDecimal;

public class CancelWorkDTO {

	private Long workId;
	private Double price;
	private String cancellationReasonTypeCode;
	private String note;

	public CancelWorkDTO() {}

	public CancelWorkDTO(Long workId, Double price, String cancellationReasonTypeCode, String note) {
		this.workId = workId;
		this.price = price;
		this.cancellationReasonTypeCode = cancellationReasonTypeCode;
		this.note = note;
	}

	public Long getWorkId() {
		return workId;
	}

	public Double getPrice() {
		return price;
	}

	public String getCancellationReasonTypeCode() {
		return cancellationReasonTypeCode;
	}

	public String getNote() {
		return note;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setCancellationReasonTypeCode(String cancellationReasonTypeCode) {
		this.cancellationReasonTypeCode = cancellationReasonTypeCode;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isPaid() {
		return getPrice() != null && new BigDecimal(getPrice()).compareTo(BigDecimal.ZERO) > 0;
	}
}

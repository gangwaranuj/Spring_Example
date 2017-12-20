package com.workmarket.service.business.dto;

public class UnassignDTO {

	private Long workId;
	private String cancellationReasonTypeCode;
	private boolean rollbackToOriginalPrice;
	private String note;

	public UnassignDTO() {}

	public UnassignDTO(String cancellationReasonTypeCode, String note) {
		this.cancellationReasonTypeCode = cancellationReasonTypeCode;
		this.note = note;
	}

	public Long getWorkId() {
		return workId;
	}

	public UnassignDTO setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}

	public String getCancellationReasonTypeCode() {
		return cancellationReasonTypeCode;
	}

	public String getNote() {
		return note;
	}

	public UnassignDTO setCancellationReasonTypeCode(String cancellationReasonTypeCode) {
		this.cancellationReasonTypeCode = cancellationReasonTypeCode;
		return this;
	}

	public boolean isRollbackToOriginalPrice() {
		return rollbackToOriginalPrice;
	}

	public UnassignDTO setRollbackToOriginalPrice(boolean rollbackToOriginalPrice) {
		this.rollbackToOriginalPrice = rollbackToOriginalPrice;
		return this;
	}

	public UnassignDTO setNote(String note) {
		this.note = note;
		return this;
	}
}

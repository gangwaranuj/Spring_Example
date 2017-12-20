package com.workmarket.service.business.dto;

import java.util.List;

public class CancelWorkNumbersDTO extends CancelWorkDTO {
	private List<String> workNumbers;

	public CancelWorkNumbersDTO(){}

	public CancelWorkNumbersDTO(Double price, String cancellationReasonTypeCode, String note, List<String> workNumbers) {
		super(null, price, cancellationReasonTypeCode, note);
		this.workNumbers = workNumbers;
	}

	public List<String> getWorkNumbers() {
		return workNumbers;
	}

	public void setWorkNumbers(List<String> workNumbers) {
		this.workNumbers = workNumbers;
	}
}

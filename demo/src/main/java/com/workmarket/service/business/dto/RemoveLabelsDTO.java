package com.workmarket.service.business.dto;

import java.util.List;

/**
 * User: micah
 * Date: 7/16/14
 * Time: 10:52 AM
 */
public class RemoveLabelsDTO {
	private List<String> workNumbers;
	private List<Long> labelIds;
	private String Note;

	public List<String> getWorkNumbers() {
		return workNumbers;
	}

	public void setWorkNumbers(List<String> workNumbers) {
		this.workNumbers = workNumbers;
	}

	public List<Long> getLabelIds() {
		return labelIds;
	}

	public void setLabelIds(List<Long> labelIds) {
		this.labelIds = labelIds;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
	}
}

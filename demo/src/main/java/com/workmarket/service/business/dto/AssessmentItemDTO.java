package com.workmarket.service.business.dto;

public class AssessmentItemDTO {
	private Long itemId;
	private String prompt;
	private String description;
	private String hint;
	private String incorrectFeedback;
	private String type;
	private Boolean otherAllowed = Boolean.FALSE;
	private Integer maxLength;
	private Boolean graded = Boolean.FALSE;
	
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public String getIncorrectFeedback() {
		return incorrectFeedback;
	}
	public void setIncorrectFeedback(String incorrectFeedback) {
		this.incorrectFeedback = incorrectFeedback;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getOtherAllowed() {
		return otherAllowed;
	}
	public void setOtherAllowed(Boolean otherAllowed) {
		this.otherAllowed = otherAllowed;
	}
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	public Boolean getGraded() {
		return graded;
	}
	public void setGraded(Boolean graded) {
		this.graded = graded;
	}
}

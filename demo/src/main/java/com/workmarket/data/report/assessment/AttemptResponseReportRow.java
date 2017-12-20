package com.workmarket.data.report.assessment;

public class AttemptResponseReportRow {
	private Integer itemId;
	private String itemPrompt;
	private String responseValue;
	
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public String getItemPrompt() {
		return itemPrompt;
	}
	public void setItemPrompt(String itemPrompt) {
		this.itemPrompt = itemPrompt;
	}
	public String getResponseValue() {
		return responseValue;
	}
	public void setResponseValue(String responseValue) {
		this.responseValue = responseValue;
	}
}

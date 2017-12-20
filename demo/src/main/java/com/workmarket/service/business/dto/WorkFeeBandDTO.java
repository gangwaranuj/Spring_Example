package com.workmarket.service.business.dto;

public class WorkFeeBandDTO {
	
	private String minimum;
	private String maximum; 
	private String percentage;
	public String getMinimum() {
		return minimum;
	}
	public String getMaximum() {
		return maximum;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setMinimum(String minimum) {
		this.minimum = minimum;
	}
	public void setMaximum(String maximum) {
		this.maximum = maximum;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	
	
}

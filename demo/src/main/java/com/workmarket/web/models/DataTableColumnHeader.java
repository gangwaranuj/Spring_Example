package com.workmarket.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataTableColumnHeader {

	@JsonProperty("sTitle")
	private String sTitle;

	@JsonProperty("sType")
	private String sType;

	@JsonProperty("aTargets")
	private Integer[] aTargets;

	public DataTableColumnHeader() {
	}

	public String getsTitle() {
		return sTitle;
	}

	public void setsTitle(String sTitle) {
		this.sTitle = sTitle;
	}

	public String getsType() {
		return sType;
	}

	public void setsType(String sType) {
		this.sType = sType;
	}

	public Integer[] getaTargets() {
		return aTargets;
	}

	public void setaTargets(Integer[] aTargets) {
		this.aTargets = aTargets;
	}
}

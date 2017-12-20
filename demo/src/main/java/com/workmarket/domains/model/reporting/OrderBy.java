package com.workmarket.domains.model.reporting;

import java.io.Serializable;

public class OrderBy implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private String column;
	private Boolean desc;
	private static final long serialVersionUID = -2134155858724981849L;

	public OrderBy(){		
	}

	public OrderBy(String column, Boolean desc){
		this.column = column;
		this.desc = desc;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Boolean getDesc() {
		return desc;
	}

	public void setDesc(Boolean desc) {
		this.desc = desc;
	}

}

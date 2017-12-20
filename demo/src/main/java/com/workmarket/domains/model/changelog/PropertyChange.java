package com.workmarket.domains.model.changelog;

import java.io.Serializable;

public class PropertyChange implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String property;
	private String oldValue;
	private String newValue;
	
	public PropertyChange(String property, String oldValue, String newValue) {
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public String getProperty() {
		return property;
	}
	public String getOldValue() {
		return oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
}

/**
 * 
 */
package com.workmarket.domains.model.reporting;

import java.io.Serializable;


public class SelectOption implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private String value;
	private String label;

	private static final long serialVersionUID = -8914288539097750690L;
	
	public SelectOption() {}
	public SelectOption(String value, String label) {
		this.value = value;
		this.label = label;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}

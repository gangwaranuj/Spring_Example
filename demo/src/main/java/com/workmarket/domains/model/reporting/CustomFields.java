package com.workmarket.domains.model.reporting;


public class CustomFields extends Entity {

	/**
	 * Instance variables and constants
	 */
	private String custom_key;

	private static final long serialVersionUID = 3465138252441096667L;

	/**
	 * @return the custom_key
	 */
	public String getCustom_key() {
		return custom_key;
	}

	/**
	 * @param custom_key the custom_key to set
	 */
	public void setCustom_key(String custom_key) {
		this.custom_key = custom_key;
	}

}

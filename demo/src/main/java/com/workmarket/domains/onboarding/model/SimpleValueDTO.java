package com.workmarket.domains.onboarding.model;

/**
 * Created by ianha on 6/26/14
 */
public class SimpleValueDTO {
	private String value;
	private boolean checked;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}

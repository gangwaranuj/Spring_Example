package com.workmarket.service.business.dto.integration;

public class AutotaskUserCustomFieldsPreferenceDTO {

	private String customFieldCode;
	private String customFieldValue;
	private boolean enabled;

	public AutotaskUserCustomFieldsPreferenceDTO() {}

	public AutotaskUserCustomFieldsPreferenceDTO(String customFieldCode, String customFieldValue, boolean enabled) {
		this.customFieldCode = customFieldCode;
		this.customFieldValue = customFieldValue;
		this.enabled = enabled;
	}

	public String getCustomFieldCode() {
		return customFieldCode;
	}

	public void setCustomFieldCode(String customFieldCode) {
		this.customFieldCode = customFieldCode;
	}

	public String getCustomFieldValue() {
		return customFieldValue;
	}

	public void setCustomFieldValue(String customFieldValue) {
		this.customFieldValue = customFieldValue;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}

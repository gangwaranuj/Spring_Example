package com.workmarket.domains.onboarding.model;

/**
 * Created by ianha on 6/26/14
 */
public class OnboardingIndustryDTO {
	private Long id;
	private String name;
	private boolean checked;
	private String otherName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getOtherName() { return otherName; }

	public void setOtherName(String otherName) { this.otherName = otherName; }
}

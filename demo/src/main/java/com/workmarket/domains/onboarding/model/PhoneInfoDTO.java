package com.workmarket.domains.onboarding.model;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by ianha on 6/27/14
 */
public class PhoneInfoDTO {
	private String type;
	private String code;
	@Pattern(regexp = "^$|(^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$)?")
	private String number;

	public PhoneInfoDTO() { }

	public PhoneInfoDTO(String type) {
		this(type, null, null);
	}

	public PhoneInfoDTO(String type, String code, String number) {
		this.type = type;
		this.code = code;
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}

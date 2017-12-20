package com.workmarket.common.template.sms;

import org.springframework.util.Assert;

public class PhoneVerificationSMSTemplate extends SMSTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6449873579871690916L;
	
	private String code;

	public PhoneVerificationSMSTemplate(Long providerId, String toNumber, String code) {
		super(providerId, toNumber);

		Assert.notNull(code);
		Assert.hasLength(code);

		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}

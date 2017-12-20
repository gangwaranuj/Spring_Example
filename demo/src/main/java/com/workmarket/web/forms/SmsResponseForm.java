package com.workmarket.web.forms;

import com.workmarket.domains.model.MobileProvider;

import java.util.List;

public class SmsResponseForm {
	private final String message;
	private final String smsPhone;
	private final Long mobileProviderId;
	private final List<MobileProvider> providers;

	public SmsResponseForm(String message) {
		this.message = message;
		this.smsPhone = null;
		this.mobileProviderId = null;
		this.providers = null;
	}

	public SmsResponseForm(String message, String smsPhone) {
		this.message = message;
		this.smsPhone = smsPhone;
		this.mobileProviderId = null;
		this.providers = null;
	}

	public SmsResponseForm(String smsPhone, Long mobileProviderId, List<MobileProvider> providers) {
		this.message = null;
		this.smsPhone = smsPhone;
		this.mobileProviderId = mobileProviderId;
		this.providers = providers;
	}

	public String getMessage() {
		return message;
	}

	public String getSmsPhone() {
		return smsPhone;
	}

	public Long getMobileProviderId() {
		return mobileProviderId;
	}

	public List<MobileProvider> getProviders() {
		return providers;
	}
}

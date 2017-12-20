package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;

public abstract class AbstractClientServicesEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -1917593114397033722L;

	protected AbstractClientServicesEmailTemplate(Long fromId) {
		super(fromId, Constants.EMAIL_CLIENT_SERVICES);
	}

	public String getTimeZoneId() {
		return Constants.DEFAULT_TIMEZONE;
	}
}

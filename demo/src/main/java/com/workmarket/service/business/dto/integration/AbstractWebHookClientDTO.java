package com.workmarket.service.business.dto.integration;

import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;

import javax.validation.constraints.NotNull;

public abstract class AbstractWebHookClientDTO {
	@NotNull AbstractWebHookClient.DateFormat dateFormat;
	boolean suppressApiEvents = true;

	public AbstractWebHookClient.DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(AbstractWebHookClient.DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean isSuppressApiEvents() {
		return suppressApiEvents;
	}

	public void setSuppressApiEvents(boolean suppressApiEvents) {
		this.suppressApiEvents = suppressApiEvents;
	}
}

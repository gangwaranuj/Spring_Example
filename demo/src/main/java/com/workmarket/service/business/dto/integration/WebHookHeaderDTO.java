package com.workmarket.service.business.dto.integration;

import com.workmarket.domains.model.integration.webhook.WebHookHeader;
import com.workmarket.utility.BeanUtilities;

public class WebHookHeaderDTO {
	Long id;
	String name;
	String value;

	public static WebHookHeaderDTO newDTO(WebHookHeader webHookHeader) {
		return BeanUtilities.newBean(WebHookHeaderDTO.class, webHookHeader);
	}

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

package com.workmarket.service.business.dto.integration;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.integration.webhook.WebHookHeader;
import com.workmarket.utility.BeanUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebHookDTO {
	private Long webHookClientId;
	private Long id;
	String integrationEventTypeCode;
	IntegrationEventType integrationEventType;
	boolean enabled;
	URL url;
	WebHook.MethodType methodType;
	WebHook.ContentType contentType;
	String body = "";
	Integer callOrder = 0;
	List<WebHookHeaderDTO> headers = Lists.newArrayList();
	Boolean suppressApiEvents;

	public static WebHookDTO newDTO(WebHook webHook) {
		WebHookDTO dto = BeanUtilities.newBean(WebHookDTO.class, webHook);
		List<WebHookHeaderDTO> webHookHeaderDTOs = new ArrayList<WebHookHeaderDTO>();

		for (WebHookHeader webHookHeader : webHook.getWebHookHeaders()) {
			webHookHeaderDTOs.add(WebHookHeaderDTO.newDTO(webHookHeader));
		}

		dto.setHeaders(webHookHeaderDTOs);
		return dto;
	}

	public String getIntegrationEventTypeCode() {
		return (integrationEventType == null) ? null : integrationEventType.getCode();
	}

	public void setIntegrationEventTypeCode(String integrationEventTypeCode) {
		this.integrationEventTypeCode = integrationEventTypeCode;
		this.integrationEventType = IntegrationEventType.newInstance(integrationEventTypeCode);
	}

	public IntegrationEventType getIntegrationEventType() {
		return integrationEventType;
	}

	public void setIntegrationEventType(IntegrationEventType integrationEventType) {
		this.integrationEventType = integrationEventType;
		this.integrationEventTypeCode = integrationEventType.getCode();
	}

	public Boolean isSuppressApiEvents() {
		return suppressApiEvents;
	}

	public void setSuppressApiEvents(Boolean suppressApiEvents) {
		this.suppressApiEvents = suppressApiEvents;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public WebHook.MethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(WebHook.MethodType methodType) {
		this.methodType = methodType;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public WebHook.ContentType getContentType() {
		return contentType;
	}

	public void setContentType(WebHook.ContentType contentType) {
		this.contentType = contentType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<WebHookHeaderDTO> getHeaders() {
		return headers;
	}

	public void setHeaders(List<WebHookHeaderDTO> headers) {
		this.headers = headers;
	}

	public Integer getCallOrder() {
		return callOrder;
	}

	public void setCallOrder(Integer callOrder) {
		this.callOrder = callOrder;
	}

	public Long getWebHookClientId() {
		return webHookClientId;
	}

	public void setWebHookClientId(Long webHookClientId) {
		this.webHookClientId = webHookClientId;
	}
}

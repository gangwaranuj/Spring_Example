package com.workmarket.service.business.integration.hooks.webhook;

import com.google.common.base.Optional;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;
import org.springframework.http.HttpStatus;

import java.util.Map;

public interface WebHookHTTPPoolingFactory {
	public Optional<ParsedWebHookDTO> buildHook(AbstractWork work, WebHook webHook, Map<String, String> contextVariables);
	public Optional<ParsedWebHookDTO> buildHook(AbstractWork work, WebHook webHook, Map<String, String> contextVariables, Map<String, String> authenticationHeaders);
	public HttpStatus launchHook(WebHook webHook, ParsedWebHookDTO parsedWebHookDTO);
}

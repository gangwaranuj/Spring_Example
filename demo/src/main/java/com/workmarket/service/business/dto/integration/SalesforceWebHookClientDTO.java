package com.workmarket.service.business.dto.integration;

import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;
import com.workmarket.utility.BeanUtilities;

public class SalesforceWebHookClientDTO extends AbstractWebHookClientDTO {
	public static SalesforceWebHookClientDTO newDTO(SalesforceWebHookClient salesforceWebHookClient) {
		return BeanUtilities.newBean(SalesforceWebHookClientDTO.class, salesforceWebHookClient);
	}
}

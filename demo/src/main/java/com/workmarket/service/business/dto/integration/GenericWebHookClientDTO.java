package com.workmarket.service.business.dto.integration;

import com.workmarket.domains.model.integration.webhook.GenericWebHookClient;
import com.workmarket.utility.BeanUtilities;

public class GenericWebHookClientDTO extends AbstractWebHookClientDTO {
	public static GenericWebHookClientDTO newDTO(GenericWebHookClient genericWebHookClient) {
		return BeanUtilities.newBean(GenericWebHookClientDTO.class, genericWebHookClient);
	}
}

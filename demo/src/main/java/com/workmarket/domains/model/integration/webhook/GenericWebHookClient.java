package com.workmarket.domains.model.integration.webhook;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="genericWebHookClient")
@DiscriminatorValue(AbstractWebHookClient.GENERIC)
@AuditChanges
public class GenericWebHookClient extends AbstractWebHookClient {
	private static final long serialVersionUID = -5846131567650849155L;

}

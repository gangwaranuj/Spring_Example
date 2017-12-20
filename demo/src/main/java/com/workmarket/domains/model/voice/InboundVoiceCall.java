package com.workmarket.domains.model.voice;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue("inbound")
@AuditChanges
public class InboundVoiceCall extends VoiceCall {
	private static final long serialVersionUID = 1L;
}

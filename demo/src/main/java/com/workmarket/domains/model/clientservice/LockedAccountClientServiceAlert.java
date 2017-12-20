package com.workmarket.domains.model.clientservice;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue("ACCT_LOCKED")
@AuditChanges
public class LockedAccountClientServiceAlert extends ClientServiceAlert {

	private static final long serialVersionUID = 1L;

}

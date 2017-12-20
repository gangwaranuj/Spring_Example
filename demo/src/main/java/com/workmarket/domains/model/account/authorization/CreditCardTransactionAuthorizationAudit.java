package com.workmarket.domains.model.account.authorization;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity(name = "creditCardAuthorizationAudit")
@DiscriminatorValue("CCA")
@AuditChanges
public class CreditCardTransactionAuthorizationAudit extends TransactionAuthorizationAudit {

	private static final long serialVersionUID = 1L;

	public CreditCardTransactionAuthorizationAudit() {
		super();
	}

}

package com.workmarket.domains.model.comment;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "clientServiceCompanyComment")
@DiscriminatorValue("CSCC")
@AuditChanges
public class ClientServiceCompanyComment extends CompanyComment {
	private static final long serialVersionUID = 1L;
}

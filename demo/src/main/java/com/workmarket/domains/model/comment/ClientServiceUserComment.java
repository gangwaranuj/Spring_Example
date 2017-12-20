package com.workmarket.domains.model.comment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;

import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "clientServiceUserComment")
@NamedQueries({
})
@DiscriminatorValue("CSUC")
@AuditChanges
public class ClientServiceUserComment extends UserComment {
	private static final long serialVersionUID = 1L;
}

package com.workmarket.domains.model.crm;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.workmarket.domains.model.directory.AbstractEntityEmailAssociation;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.directory.EntityEmailAssociation;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="clientContactEmailAssociation")
@Table(name="client_contact_email_association")
@AttributeOverride(name="entity_id", column = @Column(name="client_contact_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="client_contact_id"))
})
@AuditChanges
public class ClientContactEmailAssociation extends AbstractEntityEmailAssociation<ClientContact> implements EntityEmailAssociation<ClientContact> {

	private static final long serialVersionUID = 1L;

	public ClientContactEmailAssociation() {
		super();
	}

	public ClientContactEmailAssociation(ClientContact entity, Email email) {
		super(entity, email);
	}
}

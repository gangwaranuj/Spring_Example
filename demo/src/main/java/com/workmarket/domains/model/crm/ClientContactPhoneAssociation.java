package com.workmarket.domains.model.crm;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.workmarket.domains.model.directory.AbstractEntityPhoneAssociation;
import com.workmarket.domains.model.directory.EntityPhoneAssociation;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="clientContactPhoneAssociation")
@Table(name="client_contact_phone_association")
@AttributeOverride(name="entity_id", column = @Column(name="client_contact_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="client_contact_id"))
})
@AuditChanges
public class ClientContactPhoneAssociation extends AbstractEntityPhoneAssociation<ClientContact> implements EntityPhoneAssociation<ClientContact> {

	private static final long serialVersionUID = 1L;

	public ClientContactPhoneAssociation() {
		super();
	}

	public ClientContactPhoneAssociation(ClientContact entity, Phone phone) {
		super(entity, phone);
	}
}

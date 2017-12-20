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

@Entity(name="clientLocationPhoneAssociation")
@Table(name="client_location_phone_association")
@AttributeOverride(name="entity_id", column = @Column(name="location_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="location_id"))
})
@AuditChanges
public class ClientLocationPhoneAssociation extends AbstractEntityPhoneAssociation<ClientLocation> implements EntityPhoneAssociation<ClientLocation> {

	private static final long serialVersionUID = 1L;

	public ClientLocationPhoneAssociation() {
		super();
	}

	public ClientLocationPhoneAssociation(ClientLocation entity, Phone phone) {
		super(entity, phone);
	}
}

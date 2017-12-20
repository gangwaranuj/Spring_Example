package com.workmarket.domains.model;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.directory.AbstractEntityPhoneAssociation;
import com.workmarket.domains.model.directory.EntityPhoneAssociation;
import com.workmarket.domains.model.directory.Phone;

@Entity(name="profilePhoneAssociation")
@Table(name="profile_phone_association")
@AttributeOverride(name="entity_id", column = @Column(name="profile_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="profile_id"))
})
@AuditChanges
public class ProfilePhoneAssociation extends AbstractEntityPhoneAssociation<Profile> implements EntityPhoneAssociation<Profile> {

	private static final long serialVersionUID = 1L;

	public ProfilePhoneAssociation() {
		super();
	}

	public ProfilePhoneAssociation(Profile entity, Phone phone) {
		super(entity, phone);
	}
}

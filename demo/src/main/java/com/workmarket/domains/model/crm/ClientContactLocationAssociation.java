package com.workmarket.domains.model.crm;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * Created by arjun on 7/2/14.
 */
@Entity(name = "clientContactLocationAssociation")
@Table(name = "client_contact_location_association")
@AuditChanges
public class ClientContactLocationAssociation extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private ClientContact clientContact;
	private ClientLocation clientLocation;

	public ClientContactLocationAssociation() {}

	public ClientContactLocationAssociation(ClientContact contact, ClientLocation location) {
		this.clientContact = contact;
		this.clientLocation = location;
	}

	@ManyToOne
	@JoinColumn(name = "client_contact_id")
	@Where(clause = "deleted = 0")
	public ClientContact getClientContact() {
		return this.clientContact;
	}

	public void setClientContact(ClientContact clientContact) {
		this.clientContact = clientContact;
	}

	@ManyToOne
	@JoinColumn(name = "location_id")
	@Where(clause = "deleted = 0")
	public ClientLocation getClientLocation() {
		return this.clientLocation;
	}

	public void setClientLocation(ClientLocation clientLocation) {
		this.clientLocation = clientLocation;
	}

}

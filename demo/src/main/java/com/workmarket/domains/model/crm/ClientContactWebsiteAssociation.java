package com.workmarket.domains.model.crm;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.workmarket.domains.model.directory.AbstractEntityWebsiteAssociation;
import com.workmarket.domains.model.directory.EntityWebsiteAssociation;
import com.workmarket.domains.model.directory.Website;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="clientContactWebsiteAssociation")
@Table(name="client_contact_website_association")
@AttributeOverride(name="entity_id", column = @Column(name="client_contact_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="client_contact_id"))
})
@AuditChanges
public class ClientContactWebsiteAssociation extends AbstractEntityWebsiteAssociation<ClientContact> implements EntityWebsiteAssociation<ClientContact> {

	private static final long serialVersionUID = 1L;

	public ClientContactWebsiteAssociation() {
		super();
	}

	public ClientContactWebsiteAssociation(ClientContact entity, Website website) {
		super(entity, website);
	}
}

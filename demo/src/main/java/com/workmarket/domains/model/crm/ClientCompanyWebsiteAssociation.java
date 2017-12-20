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

@Entity(name="clientCompanyWebsiteAssociation")
@Table(name="client_company_website_association")
@AttributeOverride(name="entity_id", column = @Column(name="client_company_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="client_company_id"))
})
@AuditChanges
public class ClientCompanyWebsiteAssociation extends AbstractEntityWebsiteAssociation<ClientCompany> implements EntityWebsiteAssociation<ClientCompany> {

	private static final long serialVersionUID = 1L;

	public ClientCompanyWebsiteAssociation() {
		super();
	}

	public ClientCompanyWebsiteAssociation(ClientCompany entity, Website website) {
		super(entity, website);
	}
}

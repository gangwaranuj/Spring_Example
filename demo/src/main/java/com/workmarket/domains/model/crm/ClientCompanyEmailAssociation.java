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

@Entity(name="clientCompanyEmailAssociation")
@Table(name="client_company_email_association")
@AttributeOverride(name="entity_id", column = @Column(name="client_company_id") )
@AssociationOverrides({
	@AssociationOverride(name="entity", joinColumns = @JoinColumn(name="client_company_id"))
})
@AuditChanges
public class ClientCompanyEmailAssociation extends AbstractEntityEmailAssociation<ClientCompany> implements EntityEmailAssociation<ClientCompany> {

	private static final long serialVersionUID = 1L;

	public ClientCompanyEmailAssociation() {
		super();
	}

	public ClientCompanyEmailAssociation(ClientCompany entity, Email email) {
		super(entity, email);
	}
}

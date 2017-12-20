package com.workmarket.domains.model.crm;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "clientCompanyResourceAssociation")
@Table(name = "client_company_resource_association")
@AuditChanges
public class ClientCompanyResourceAssociation extends DeletableEntity {

	private static final long serialVersionUID = 8113128199292911825L;

	private ClientCompany clientCompany;
	private User resource;

	public ClientCompanyResourceAssociation() {	}

	public ClientCompanyResourceAssociation(ClientCompany clientCompany, User resource) {
		this.clientCompany = clientCompany;
		this.resource = resource;
	}

	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="client_company_id", referencedColumnName="id", nullable=false)
	public ClientCompany getClientCompany() {
		return clientCompany;
	}

	public void setClientCompany(ClientCompany clientCompany) {
		this.clientCompany = clientCompany;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	public User getResource() {
		return resource;
	}

	public void setResource(User resource) {
		this.resource = resource;
	}

}

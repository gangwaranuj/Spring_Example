package com.workmarket.domains.model.acl;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.network.CompanyNetworkAssociation;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "company_role_association")
@NamedQueries({
	@NamedQuery(name="networkacl.findAclNetworkRoleAssociationsByCompanyId",
							query="select anra from AclNetworkRoleAssociation anra where anra.companyNetworkAssociation.company.id = :companyId "),
	@NamedQuery(name="networkacl.softDeleteAclNetworkRoleAssociationsByCompanyId",
		query="update AclNetworkRoleAssociation anra set anra.deleted = true where anra.companyNetworkAssociation.company.id = :companyId "
	)
})
@AuditChanges
public class AclNetworkRoleAssociation extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private CompanyNetworkAssociation companyNetworkAssociation;
	private AclRole role;
	private boolean active;

	@ManyToOne(optional = false)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "company_network_association_id")
	public CompanyNetworkAssociation getCompanyNetworkAssociation() {
		return companyNetworkAssociation;
	}
	public void setCompanyNetworkAssociation(CompanyNetworkAssociation companyNetworkAssociation) {
		this.companyNetworkAssociation = companyNetworkAssociation;
	}

	@ManyToOne(optional = false)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "role_id")
	public AclRole getRole() {
		return role;
	}
	public void setRole(AclRole role) {
		this.role = role;
	}

	@Column(name = "active")
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}

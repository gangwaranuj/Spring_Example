package com.workmarket.domains.model.network;

import com.workmarket.domains.model.ActiveDeletableEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.acl.AclNetworkRoleAssociation;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * Created by ant on 7/22/14.
 */

@Entity(name="companyNetworkAssociation")
@Table(name="company_network_association")
@AuditChanges
public class CompanyNetworkAssociation extends ActiveDeletableEntity {

	private static final long serialVersionUID = -2154037609404469646L;

	private Set<AclNetworkRoleAssociation> networkRoleAssociations;
	private Company company;
	private Network network;

	@OneToMany(cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "company_network_association_id")
	public Set<AclNetworkRoleAssociation> getNetworkRoleAssociations() {
		return networkRoleAssociations;
	}
	public void setNetworkRoleAssociations(Set<AclNetworkRoleAssociation> networkRoleAssociation) {
		this.networkRoleAssociations = networkRoleAssociation;
	}

	@ManyToOne(optional = false)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "company_id")
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(optional = false)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "network_id")
	public Network getNetwork() {
		return network;
	}
	public void setNetwork(Network network) {
		this.network = network;
	}

}

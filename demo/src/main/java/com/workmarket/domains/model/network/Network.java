package com.workmarket.domains.model.network;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.ActiveDeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.Set;

/**
 * Created by ant on 7/22/14.
 */

@Entity
@Table(name="network")
@NamedQueries({
	@NamedQuery(name="network.findAll", query="select n from Network n "),
	@NamedQuery(name="network.findNetworkById", query="select n from Network n where id = :id"),
	@NamedQuery(name="network.findActiveNetworksByCompany", query="select n from Network n join fetch n.companyNetworkAssociations cna where cna.company.id = :companyId and cna.active = true order by n.name asc"),
	@NamedQuery(name="network.softDeleteNetworks", query="update Network set active = false where id = :networkId"),
	@NamedQuery(name="network.removeGroupFromNetwork", query="update userGroupNetworkAssociation ugna set deleted = 1, active = 0 where ugna.network.id = :networkId and ugna.userGroup.id = :groupId"),
	@NamedQuery(name="network.removeGroupFromAllNetworks", query="update userGroupNetworkAssociation ugna set ugna.deleted = 1, ugna.active = 0 where ugna.userGroup.id = :groupId and ugna.network.id IN (:ids)"),
	@NamedQuery(
		name="network.findGroupsNetworks",
		query=
			"select n from Network n join fetch n.userGroupNetworkAssociations ugna " +
			"where ugna.userGroup.id = :groupId " +
			"and ugna.active = true and ugna.deleted = false " +
			"and n.deleted = false and n.active = true "
	)
})

@AuditChanges
public class Network extends ActiveDeletableEntity {

	private static final long serialVersionUID = 8286542111164381385L;
	private String name;
	private Set<CompanyNetworkAssociation> companyNetworkAssociations = Sets.newLinkedHashSet();
	private Set<UserNetworkAssociation> userNetworkAssociations = Sets.newLinkedHashSet();
	private Set<UserGroupNetworkAssociation> userGroupNetworkAssociations = Sets.newLinkedHashSet();

	@Column(name="name", nullable = false, length = 200)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="network_id")
	public Set<CompanyNetworkAssociation> getCompanyNetworkAssociations() {
		return companyNetworkAssociations;
	}
	public void setCompanyNetworkAssociations(Set<CompanyNetworkAssociation> companyNetworkAssociations) {
		this.companyNetworkAssociations = companyNetworkAssociations;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="network_id")
	public Set<UserNetworkAssociation> getUserNetworkAssociations() {
		return userNetworkAssociations;
	}
	public void setUserNetworkAssociations(Set<UserNetworkAssociation> userNetworkAssociations) {
		this.userNetworkAssociations = userNetworkAssociations;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="network_id")
	public Set<UserGroupNetworkAssociation> getUserGroupNetworkAssociations() {
		return userGroupNetworkAssociations;
	}
	public void setUserGroupNetworkAssociations(Set<UserGroupNetworkAssociation> userGroupNetworkAssociations) {
		this.userGroupNetworkAssociations = userGroupNetworkAssociations;
	}

	@Override
	public String toString() {
		return "Network{" +
			"name='" + name + '\'' +
			'}';
	}
}

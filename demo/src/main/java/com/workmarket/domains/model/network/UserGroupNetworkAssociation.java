package com.workmarket.domains.model.network;

import com.workmarket.domains.model.ActiveDeletableEntity;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by ant on 7/22/14.
 */

@Entity(name="userGroupNetworkAssociation")
@Table(name="user_group_network_association")
@AuditChanges
public class UserGroupNetworkAssociation extends ActiveDeletableEntity {

	private static final long serialVersionUID = 4403856889386176006L;
	private UserGroup userGroup;
	private Network network;

	@ManyToOne(optional = false)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "user_group_id")
	public UserGroup getUserGroup() {
		return userGroup;
	}
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
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

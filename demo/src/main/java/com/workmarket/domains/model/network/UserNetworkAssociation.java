package com.workmarket.domains.model.network;

import com.workmarket.domains.model.ActiveDeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * Created by ant on 7/22/14.
 */

@Entity
@Table(name="worker_network_association")

@NamedQueries({
	@NamedQuery(name="userNetworkAssociation.softDeleteUserNetworkAssocation", query="update UserNetworkAssociation set active = false where worker.id = :workerId and network.id = :networkId "),
	@NamedQuery(name="userNetworkAssociation.findActiveUsersInNetworkById", query="select una from UserNetworkAssociation una where network.id = :networkId and active = true order by worker.lastName "),
	@NamedQuery(name="userNetworkAssociation.removeUserFromAllNetworks", query="update UserNetworkAssociation set active = false where worker.id = :workerUserId and network.id in (:networkIds) ")

})
@AuditChanges
public class UserNetworkAssociation extends ActiveDeletableEntity {

	private static final long serialVersionUID = 5341656173950762028L;
	private User worker;
	private Network network;

	@ManyToOne(optional = false)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "worker_user_id")
	public User getWorker() {
		return worker;
	}
	public void setWorker(User worker) {
		this.worker = worker;
	}

	@ManyToOne(optional = false)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "network_id")
	public Network getNetwork() {
		return network;
	}
	public void setNetwork(Network phone) {
		this.network = phone;
	}

	@Override
	public String toString() {
		return "UserNetworkAssociation{" +
			"worker=" + worker +
			'}';
	}
}

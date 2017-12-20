package com.workmarket.domains.model.requirementset;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractExpirableRequirement extends AbstractRequirement {
	private boolean notifyOnExpiry = true;
	private boolean removeMembershipOnExpiry = true;

	@Column(name = "notify_on_expiry")
	public boolean isNotifyOnExpiry() {
		return notifyOnExpiry;
	}

	public void setNotifyOnExpiry(boolean notifyOnExpiry) {
		this.notifyOnExpiry = notifyOnExpiry;
	}

	@Column(name = "remove_membership_on_expiry")
	public boolean isRemoveMembershipOnExpiry() {
		return removeMembershipOnExpiry;
	}

	public void setRemoveMembershipOnExpiry(boolean removeMembershipOnExpiry) {
		this.removeMembershipOnExpiry = removeMembershipOnExpiry;
	}
}

package com.workmarket.domains.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * primary key for WorkVendorInvitationToGroupAssociation.
 */
@Embeddable
@Access(AccessType.PROPERTY)
public class WorkVendorInvitationUserGroupPK implements Serializable {
	private Long workVendorInvitationId;
	private Long userGroupId;

	public WorkVendorInvitationUserGroupPK() {
	}

	public WorkVendorInvitationUserGroupPK(final Long workVendorInvitationId, final Long userGroupId) {
		this.workVendorInvitationId = workVendorInvitationId;
		this.userGroupId = userGroupId;
	}

	@Column(name = "work_vendor_invitation_id", nullable = false)
	public Long getWorkVendorInvitationId() {
		return workVendorInvitationId;
	}

	@Column(name = "user_group_id", nullable = false)
	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setWorkVendorInvitationId(final Long workVendorInvitationId) {
		this.workVendorInvitationId = workVendorInvitationId;
	}

	public void setUserGroupId(final Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final WorkVendorInvitationUserGroupPK that = (WorkVendorInvitationUserGroupPK) o;

		return userGroupId != null ? userGroupId.equals(that.userGroupId) : that.userGroupId == null &&
			(workVendorInvitationId != null ? workVendorInvitationId.equals(that.workVendorInvitationId) : that.workVendorInvitationId == null);
	}

	@Override
	public int hashCode() {
		int result = userGroupId != null ? userGroupId.hashCode() : 0;
		result = 31 * result + (workVendorInvitationId != null ? workVendorInvitationId.hashCode() : 0);
		return result;
	}
}

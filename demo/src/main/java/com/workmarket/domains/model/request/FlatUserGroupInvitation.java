package com.workmarket.domains.model.request;

import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

@Entity(name="flatRequestGroupInvitation")
@Table(name="request_group_invitation")
@AuditChanges
public class FlatUserGroupInvitation extends FlatRequest {

	private static final long serialVersionUID = 1L;

	private Long userGroupId;
	private UserGroupInvitationType invitationType = UserGroupInvitationType.NEW;

	public FlatUserGroupInvitation() {}

	public FlatUserGroupInvitation(Long requestorId, Long invitedUserId, Long userGroupId, UserGroupInvitationType invitationType) {
		super(requestorId, invitedUserId);
		this.userGroupId = userGroupId;
		this.invitationType = invitationType;
	}

	public FlatUserGroupInvitation(Long requestorId, Long invitationId, Long userGroupId) {
		super(requestorId, null);
		setInvitationId(invitationId);
		this.userGroupId = userGroupId;
	}

	@Column(name = "user_group_id")
	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	@Column(name = "invitation_type", length = 25, nullable = false)
	@Enumerated(value=EnumType.STRING)
	public UserGroupInvitationType getInvitationType() {

		return invitationType;
	}

	public void setInvitationType(UserGroupInvitationType invitationType)
	{
		this.invitationType = invitationType;
	}

	@Override
	public String toString() {
		return "FlatUserGroupInvitation{" +
			super.toString() + ", " +
			"userGroupId=" + userGroupId +
			", invitationType=" + invitationType +
			'}';
	}
}

package com.workmarket.domains.model.request;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="requestGroupInvitation")
@Table(name="request_group_invitation")
@AuditChanges
public class UserGroupInvitation extends Request {

	private static final long serialVersionUID = 1L;

	private UserGroup userGroup;
	private UserGroupInvitationType invitationType = UserGroupInvitationType.NEW;

	public UserGroupInvitation() {}

	public UserGroupInvitation(User requestor, User invitedUser, UserGroup userGroup, UserGroupInvitationType invitationType) {
		super(requestor, invitedUser);
		this.userGroup = userGroup;
		this.invitationType = invitationType;
	}

	public UserGroupInvitation(User requestor, Invitation invitation, UserGroup userGroup) {
		super(requestor, invitation);
		this.userGroup = userGroup;
	}

	@ManyToOne
	@JoinColumn(name = "user_group_id")
	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Column(name = "invitation_type", length = 25, nullable = false)
	@Enumerated(value=EnumType.STRING)
	public UserGroupInvitationType getInvitationType()
	{
		return invitationType;
	}

	public void setInvitationType(UserGroupInvitationType invitationType)
	{
		this.invitationType = invitationType;
	}
}

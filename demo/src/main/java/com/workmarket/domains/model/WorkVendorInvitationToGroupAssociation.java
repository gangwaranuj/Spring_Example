package com.workmarket.domains.model;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Calendar;

@Entity(name = "workVendorInvitationToGroupAssociation")
@Table(name = "work_vendor_invitation_to_group_association")
@AuditChanges
@Access(AccessType.PROPERTY)
public class WorkVendorInvitationToGroupAssociation implements Serializable {
	private static final long serialVersionUID = -7093884857248353737L;

	private WorkVendorInvitation workVendorInvitation;
	private UserGroup userGroup;
	private Calendar modifiedOn;
	private WorkVendorInvitationUserGroupPK workVendorInvitationUserGroup;

	public WorkVendorInvitationToGroupAssociation() {

	}

	public WorkVendorInvitationToGroupAssociation(final WorkVendorInvitation workVendorInvitation, final UserGroup userGroup) {
		this.workVendorInvitationUserGroup = new WorkVendorInvitationUserGroupPK(workVendorInvitation.getId(), userGroup.getId());
	}

	public WorkVendorInvitationToGroupAssociation(final Long workVendorInvitationId, final Long userGroupId) {
		this.workVendorInvitationUserGroup = new WorkVendorInvitationUserGroupPK(workVendorInvitationId, userGroupId);
	}

	@EmbeddedId
	public WorkVendorInvitationUserGroupPK getWorkVendorInvitationUserGroup() {
		return workVendorInvitationUserGroup;
	}

	@ManyToOne
	@JoinColumn(name = "work_vendor_invitation_id", referencedColumnName = "id")
	@MapsId("workVendorInvitationUserGroup")
	public WorkVendorInvitation getWorkVendorInvitation() {
		return workVendorInvitation;
	}

	@ManyToOne
	@JoinColumn(name = "user_group_id")
	@MapsId("workVendorInvitationUserGroup")
	public UserGroup getUserGroup() {
		return userGroup;
	}

	@Column(name = "modified_on", nullable = false)
	public Calendar getModifiedOn() {
		return modifiedOn;
	}

	public void setWorkVendorInvitationUserGroup(final WorkVendorInvitationUserGroupPK workVendorInvitationUserGroup) {
		this.workVendorInvitationUserGroup = workVendorInvitationUserGroup;
	}

	public void setWorkVendorInvitation(final WorkVendorInvitation workVendorInvitation) {
		this.workVendorInvitation = workVendorInvitation;
	}

	public void setUserGroup(final UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public void setModifiedOn(final Calendar modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
}

package com.workmarket.domains.groups.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.workmarket.domains.model.ApprovableVerifiableEntity;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "userUserGroupAssociation")
@Table(name = "user_user_group_association")
@NamedQueries({
		@NamedQuery(
				name = "userUserGroupAssociation.findUserUserGroupAssociationById",
				query = "from userUserGroupAssociation e where e.id = :userUserGroupAssociationId"),
		@NamedQuery(
				name = "userUserGroupAssociation.findUserUserGroupAssociationByUserGroupIdAndUserId",
				query = "from userUserGroupAssociation e where e.userGroup.id = :userGroupId and e.user.id IN (:userIds)"),
		@NamedQuery(
				name = "userUserGroupAssociation.findCompanyOwnedGroupsHavingUserAsMember",
				query = "from userUserGroupAssociation uuga " +
						"inner join fetch uuga.userGroup ug " +
						"where ug.company.id = :companyId "+
						"and ug.deleted = 0 " +
						"and uuga.deleted = 0 " +
						"and uuga.user.id = :userId " +
						"and uuga.approvalStatus < 2"),
		@NamedQuery(
				name = "userUserGroupAssociation.findAllAssociationsByGroupIdAndUsers",
				query = "from userUserGroupAssociation uuga " +
						"where uuga.userGroup.id = :userGroupId " +
						"AND uuga.user.id IN (:userIds)" +
						"AND uuga.deleted = 0"
		)
})
@AuditChanges
public class UserUserGroupAssociation extends ApprovableVerifiableEntity {

	private static final long serialVersionUID = 1L;

	private User user;
	private UserGroup userGroup;
	private Boolean invitedFlag = Boolean.FALSE;

	private Calendar dateApplied;
	private Calendar dateApproved;
	private Calendar dateInvited;

	private Double requirementsFitScore;

	private Integer version;

	private boolean overrideMember = Boolean.FALSE;

	public UserUserGroupAssociation() {
	}

	public UserUserGroupAssociation(User user, UserGroup userGroup) {
		this.user = user;
		this.userGroup = userGroup;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_group_id")
	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Column(name = "invited_flag")
	public Boolean getInvitedFlag() {
		return invitedFlag;
	}

	public void setInvitedFlag(Boolean invitedFlag) {
		this.invitedFlag = invitedFlag;
	}

	/**
	 * @return the overrideMember
	 */
	@Column(name = "override_member")
	public boolean isOverrideMember() {
		return overrideMember;
	}

	/**
	 * @param overrideMember the overrideMember to set
	 */
	public void setOverrideMember(boolean overrideMember) {
		this.overrideMember = overrideMember;
	}

	@Column(name = "date_applied")
	public Calendar getDateApplied() {
		return dateApplied;
	}

	public void setDateApplied(Calendar dateApplied) {
		this.dateApplied = dateApplied;
	}

	@Column(name = "date_approved")
	public Calendar getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Calendar dateApproved) {
		this.dateApproved = dateApproved;
	}

	@Column(name = "date_invited")
	public Calendar getDateInvited() {
		return dateInvited;
	}

	public void setDateInvited(Calendar dateInvited) {
		this.dateInvited = dateInvited;
	}

	@Column(name = "requirements_fit_score")
	public Double getRequirementsFitScore() {
		return requirementsFitScore;
	}

	public void setRequirementsFitScore(Double requirementsFitScore) {
		this.requirementsFitScore = requirementsFitScore;
	}

	@Override
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		if (!ApprovalStatus.APPROVED.equals(this.getApprovalStatus()) && ApprovalStatus.APPROVED.equals(approvalStatus)) {
			setDateApproved(DateUtilities.getCalendarNow());
		}
		super.setApprovalStatus(approvalStatus);
	}

	@Version
	@Column(name = "version")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Transient
	public boolean isActive() {
		return ApprovalStatus.APPROVED.equals(getApprovalStatus()) && VerificationStatus.VERIFIED.equals(getVerificationStatus());
	}

	@Transient
	public boolean isApproved() {
		return getApprovalStatus().isApproved();
	}

	@Transient
	public boolean isPending() {
		return getApprovalStatus().isPending();
	}

	@Transient
	public boolean isDeclined() {
		return getApprovalStatus().isDeclined();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || this.getClass() != o.getClass()) { return false; }

		UserUserGroupAssociation that = (UserUserGroupAssociation)o;

		UserGroup thisUserGroup = this.getUserGroup();
		User thisUser = this.getUser();
		UserGroup thatUserGroup = that.getUserGroup();
		User thatUser = that.getUser();

		if (thisUserGroup == null && thatUserGroup == null && thisUser == null && thatUser == null) { return true; }

		if ((thisUserGroup == null && thatUserGroup != null) || (thisUserGroup != null && thatUserGroup == null)) { return false; }
		if ((thisUser == null && thatUser != null) || (thisUser != null && thatUser == null)) { return false; }
		if (thisUserGroup != null && thisUserGroup.getId() != null && !thisUserGroup.getId().equals(thatUserGroup.getId())) { return false; }
		if (thisUser != null && thisUser.getId() != null && !thisUser.getId().equals(thatUser.getId())) { return false; }

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((this.userGroup != null && this.userGroup.getId() != null) ? this.userGroup.getId().hashCode() : 0);
		result = prime * result + ((this.user != null && this.user.getId() != null) ? this.user.getId().hashCode() : 0);

		return result;
	}
}

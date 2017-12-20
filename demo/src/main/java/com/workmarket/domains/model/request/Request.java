package com.workmarket.domains.model.request;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;
import java.util.Calendar;

@AuditChanges
@Entity(name="request")
@Table(name="request")
@Inheritance(strategy=InheritanceType.JOINED)
@NamedQueries({	// TODO: these should go in their respective subclasses, doing it this way breaks abstraction
	@NamedQuery(name="request.byRequestor", query="from request where requestor.id = :requestor_user_id and deleted = false"),
	@NamedQuery(name="request.byInvitedUser", query="from request where invitedUser.id = :invited_user_id and deleted = false"),
	@NamedQuery(name="request.byInvitation", query="from request where invitation.id = :invitation_id and deleted = false"),

	@NamedQuery(name="request.assessmentInvitationByInvitedUser", query="from requestAssessmentInvitation r join fetch r.assessment where r.invitedUser.id = :invited_user_id and r.requestStatusType.code = 'sent' and r.deleted = false"),
	@NamedQuery(name="request.assessmentInvitationByAssessment", query="from requestAssessmentInvitation r join fetch r.assessment where r.assessment.id = :assessment_id and r.requestStatusType.code = 'sent' and r.deleted = false"),
	@NamedQuery(name="request.assessmentInvitationByInvitedUserAndAssessment", query="from requestAssessmentInvitation r join fetch r.assessment where r.invitedUser.id = :invited_user_id and r.assessment.id = :assessment_id and r.requestStatusType.code = 'sent' and r.deleted = false order by r.id asc"),

	@NamedQuery(name="request.groupInvitationByInvitedUser", query="from requestGroupInvitation r join fetch r.userGroup where r.invitedUser.id = :invited_user_id and r.requestStatusType.code = 'sent' and r.deleted = false"),
	@NamedQuery(name="request.groupInvitationByUserGroup", query="from requestGroupInvitation r join fetch r.userGroup where r.userGroup.id = :user_group_id and r.requestStatusType.code = 'sent' and r.deleted = false"),
	@NamedQuery(name="request.groupInvitationByInvitedUserAndUserGroup", query="from requestGroupInvitation r join fetch r.userGroup where r.invitedUser.id = :invited_user_id and r.userGroup.id = :user_group_id and r.requestStatusType.code = 'sent' and r.deleted = false order by r.id asc"),
	@NamedQuery(name="request.groupInvitationByInvitedUsersAndUserGroup", query="from requestGroupInvitation r join fetch r.userGroup join fetch r.invitedUser where r.invitedUser.id in (:invited_user_ids) and r.userGroup.id = :user_group_id and r.requestStatusType.code = 'sent' and r.deleted = false order by r.id asc"),
	@NamedQuery(name="request.allGroupInvitationsByInvitedUsersAndUserGroup", query="from requestGroupInvitation r join fetch r.userGroup join fetch r.invitedUser where r.invitedUser.id in (:invited_user_ids) and r.userGroup.id = :user_group_id and r.deleted = false order by r.id asc"),
	@NamedQuery(name="request.latestSentPasswordResetRequestByInvitedUser", query="from requestPasswordReset r where r.invitedUser.id = :invited_user_id and r.requestStatusType.code = 'sent' and r.deleted = false order by r.createdOn asc")
})
public class Request extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private User requestor;
	private User invitedUser;
	private Invitation invitation;
	private Calendar requestDate = Calendar.getInstance();
	private RequestStatusType requestStatusType;

	public Request() {}

	public Request(User requestor, User invitedUser) {
		this.requestor = requestor;
		this.invitedUser = invitedUser;
	}

	public Request(User requestor, Invitation invitation) {
		this.requestor = requestor;
		this.invitation = invitation;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "requestor_user_id", nullable=false)
	public User getRequestor() {
		return requestor;
	}

	public void setRequestor(User requestor) {
		this.requestor = requestor;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="invitee_user_id", nullable=true)
	public User getInvitedUser() {
		return invitedUser;
	}

	public void setInvitedUser(User invitedUser) {
		this.invitedUser = invitedUser;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="invitation_id", nullable=true)
	public Invitation getInvitation() {
		return invitation;
	}

	public void setInvitation(Invitation invitation) {
		this.invitation = invitation;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="request_status_type_code", referencedColumnName="code")
	public RequestStatusType getRequestStatusType() {
		return requestStatusType;
	}

	public void setRequestStatusType(RequestStatusType requestStatusType) {
		this.requestStatusType = requestStatusType;
	}

	@Column(name="request_date")
	public Calendar getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}

	@Transient
	public boolean isAccepted() {
		return requestStatusType != null && RequestStatusType.ACCEPTED.equals(requestStatusType.getCode());
	}

	@Transient
	public boolean isSent() {
		return requestStatusType != null && RequestStatusType.SENT.equals(requestStatusType.getCode());
	}
}

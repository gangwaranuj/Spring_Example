package com.workmarket.domains.model.request;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;
import java.util.Calendar;

@AuditChanges
@Entity(name="flatRequest")
@Table(name="request")
@Inheritance(strategy=InheritanceType.JOINED)
public class FlatRequest extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private Long requestorId;
	private Long invitedUserId;
	private Long invitationId;
	private Calendar requestDate = Calendar.getInstance();
	private String requestStatusTypeCode;

	public FlatRequest() {}

	public FlatRequest(Long requestorId, Long invitedUserId) {
		this.requestorId = requestorId;
		this.invitedUserId = invitedUserId;
	}

	@Column(name = "requestor_user_id", nullable=false)
	public Long getRequestorId() {
		return requestorId;
	}

	public void setRequestorId(Long requestorId) {
		this.requestorId = requestorId;
	}

	@Column(name="invitee_user_id", nullable=true)
	public Long getInvitedUserId() {
		return invitedUserId;
	}

	public void setInvitedUserId(Long invitedUserId) {
		this.invitedUserId = invitedUserId;
	}

	@Column(name="invitation_id", nullable=true)
	public Long getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(Long invitationId) {
		this.invitationId = invitationId;
	}

	@Column(name="request_status_type_code")
	public String getRequestStatusTypeCode() {
		return requestStatusTypeCode;
	}

	public void setRequestStatusTypeCode(String requestStatusType) {
		this.requestStatusTypeCode = requestStatusType;
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
		return requestStatusTypeCode != null && RequestStatusType.ACCEPTED.equals(requestStatusTypeCode);
	}

	@Transient
	public boolean isSent() {
		return requestStatusTypeCode != null && RequestStatusType.SENT.equals(requestStatusTypeCode);
	}

	@Override
	public String toString() {
		return "FlatRequest{" +
			"requestorId=" + requestorId +
			", invitedUserId=" + invitedUserId +
			", invitationId=" + invitationId +
			", requestDate=" + requestDate +
			", requestStatusTypeCode='" + requestStatusTypeCode + '\'' +
			'}';
	}
}

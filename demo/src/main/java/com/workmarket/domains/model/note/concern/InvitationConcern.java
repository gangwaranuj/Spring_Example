package com.workmarket.domains.model.note.concern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.model.Invitation;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="invitationConcern")
@DiscriminatorValue("invitation")
@AuditChanges
public class InvitationConcern extends Concern {

	private static final long serialVersionUID = 1L;

	private Invitation invitation;
	private String userName;
	private String email;

	public InvitationConcern() {
		super();
	}

	public InvitationConcern(String message, Invitation invitation) {
		super(message);
		this.invitation = invitation;
	}

    @ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="invitation_id", referencedColumnName="id")
	public Invitation getInvitation() {
		return invitation;
	}

	public void setInvitation(Invitation invitation) {
		this.invitation = invitation;
	}

	@Column(name = "user_name", length = 255)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "email", length = Constants.EMAIL_MAX_LENGTH)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Transient
	public String getType() {
		return "invitation";
	}

	@Override
	@Transient
	public Long getEntityId() {
		return invitation.getId();
	}

	@Override
	@Transient
	public String getEntityNumber() {
		return invitation.getId().toString();
	}

}

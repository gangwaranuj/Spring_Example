package com.workmarket.domains.model.request;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="requestPasswordReset")
@Table(name="request_password_reset")
@AuditChanges
public class PasswordResetRequest extends Request {

	private static final long serialVersionUID = 1L;

	private Calendar expiresOn;

	public PasswordResetRequest() {}
	public PasswordResetRequest(User requestor, User invitedUser, Calendar expiresOn) {
		super(requestor, invitedUser);
		this.expiresOn = expiresOn;
	}

	@Column(name="expires_on")
	public Calendar getExpiresOn() {
		return expiresOn;
	}

	public void setExpiresOn(Calendar expiresOn) {
		this.expiresOn = expiresOn;
	}

	@Transient
	public Boolean isExpired() {
		return !(Calendar.getInstance().compareTo(expiresOn) < 0);
	}
}

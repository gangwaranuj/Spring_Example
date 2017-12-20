package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("UBBU")
@AuditChanges
public class UserBlockedByUserChangeLog extends UserChangeLog {
	private static final long serialVersionUID = 1L;

	@NotNull
	public User blockingUser;

	public UserBlockedByUserChangeLog() {
	}

	public UserBlockedByUserChangeLog(User blockingUser) {
		this.blockingUser = blockingUser;
	}

	public UserBlockedByUserChangeLog(Long user, Long actor, Long masqueradeActor, User blockingUser) {
		super(user, actor, masqueradeActor);
		this.blockingUser = blockingUser;
	}

	@ManyToOne(cascade = {}, optional = false)
	@JoinColumn(name = "blocking_user_id", nullable = false, unique = false)
	public User getBlockingUser() {
		return blockingUser;
	}

	public void setBlockingUser(User blockingUser) {
		this.blockingUser = blockingUser;
	}

	@Transient
	@Override
	public String getDescription() {
		return "Blocked by user";
	}
}

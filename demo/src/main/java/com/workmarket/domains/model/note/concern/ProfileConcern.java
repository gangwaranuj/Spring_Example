package com.workmarket.domains.model.note.concern;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="profileConcern")
@DiscriminatorValue("profile")
@AuditChanges
public class ProfileConcern extends Concern {

	private static final long serialVersionUID = 1L;

	private User user;

	public ProfileConcern() {
		super();
	}

	public ProfileConcern(String message, User user) {
		super(message);
		this.setUser(user);
	}

	@ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="user_id", referencedColumnName="id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Transient
	public String getType() {
		return "profile";
	}

	@Override
	@Transient
	public Long getEntityId() {
		return user.getId();
	}

	@Override
	@Transient
	public String getEntityNumber() {
		return user.getUserNumber();
	}
}

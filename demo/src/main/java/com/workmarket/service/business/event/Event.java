package com.workmarket.service.business.event;

import java.io.Serializable;

import com.workmarket.service.web.AbstractWebRequestContextAware;
import org.springframework.util.Assert;

import com.workmarket.domains.model.User;

public class Event extends AbstractWebRequestContextAware implements Serializable {

	private static final long serialVersionUID = -5431476161747424749L;

	// TODO: Alex - remove references to entities in Events

	@Deprecated
	private User user;
	@Deprecated
	private User masqueradeUser;
	@Deprecated
	private User onBehalfOfUser;
	private String messageGroupId;

	public Event() {}

	public Event(User user, User masqueradeUser, User onBehalfOfUser) {
		Assert.notNull(user);
		this.user = user;
		this.masqueradeUser = masqueradeUser;
		this.onBehalfOfUser = onBehalfOfUser;
	}

	public User getUser() {
		return user;
	}

	public Event setUser(User user) {
		this.user = user;
		return this;
	}

	public User getMasqueradeUser() {
		return masqueradeUser;
	}

	public void setMasqueradeUser(User masqueradeUser) {
		this.masqueradeUser = masqueradeUser;
	}

	public User getOnBehalfOfUser() {
		return onBehalfOfUser;
	}

	public void setOnBehalfOfUser(User onBehalfOfUser) {
		this.onBehalfOfUser = onBehalfOfUser;
	}

	public String getMessageGroupId() {
		return messageGroupId;
	}

	public void setMessageGroupId(String messageGroupId) {
		this.messageGroupId = messageGroupId;
	}


	@Override
	public String toString() {
		String userStr = createUserIdStr(user);
		String masqStr = createUserIdStr(masqueradeUser);
		String onBehStr = createUserIdStr(onBehalfOfUser);
		
		return getClass().getSimpleName() + " [user=" + userStr + ", masqueradeUser=" + masqStr
				+ ", onBehalfOfUser=" + onBehStr + "]";
	}

	private String createUserIdStr(User user2) {
		if (user2 == null) {
			return null;
		} else if (user2.getId() != null) {
			return String.valueOf(user2.getId());
		}
		return null;
	}
}

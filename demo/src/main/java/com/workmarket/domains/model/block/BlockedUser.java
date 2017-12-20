package com.workmarket.domains.model.block;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.workmarket.domains.model.User;

@Embeddable
public class BlockedUser implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private User user;
	
	public BlockedUser() {}
	
	public BlockedUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="blocked_user_id", nullable=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}

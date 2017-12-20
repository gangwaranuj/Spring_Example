package com.workmarket.domains.model.comment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.validation.constraints.NotNull;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "userComment")
@NamedQueries({
})
@DiscriminatorValue("UC")
@AuditChanges
public class UserComment extends Comment {
	private static final long serialVersionUID = 1L;
	@NotNull
	private User user;

	@ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}

package com.workmarket.api.internal.model;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.utility.RandomUtilities;

import javax.persistence.*;

@Entity(name="apiKey")
@Table(name="api_key")
@AuditChanges
public class AccessKey extends AuditedEntity {
	private static final long serialVersionUID = 1L;

	private String token;
	private String secret;
	private User user;

	@Column(name="token", nullable=false, length=Constants.API_TOKEN_LENGTH)
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Column(name="secret", nullable=false, length=Constants.API_SECRET_LENGTH)
	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static AccessKey generateKey(User user) {
		AccessKey key = new AccessKey();
		key.setUser(user);
		key.setToken(RandomUtilities.generateAlphaNumericString(Constants.API_TOKEN_LENGTH));
		key.setSecret(RandomUtilities.generateAlphaNumericString(Constants.API_SECRET_LENGTH));
		return key;
	}
}

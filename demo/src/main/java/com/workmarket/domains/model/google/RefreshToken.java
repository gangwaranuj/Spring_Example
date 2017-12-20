package com.workmarket.domains.model.google;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.oauth.OAuthTokenProviderType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

@Entity(name = "refreshToken")
@Table(name = "refresh_token")
@AuditChanges
public class RefreshToken extends AuditedEntity {
	private String refreshToken;
	private String accountEmail;
	private OAuthTokenProviderType providerType;

	private User user;

	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="user_id", referencedColumnName="id", updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="provider_type_code", referencedColumnName="code", nullable=false)
	public OAuthTokenProviderType getProviderType() {
		return providerType;
	}

	public void setProviderType(OAuthTokenProviderType providerType) {
		this.providerType = providerType;
	}

	@Column(name="refresh_token", nullable=true)
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Column(name="account_email", nullable=true)
	public String getAccountEmail() {
		return accountEmail;
	}

	public void setAccountEmail(String accountEmail) {
		this.accountEmail = accountEmail;
	}
}

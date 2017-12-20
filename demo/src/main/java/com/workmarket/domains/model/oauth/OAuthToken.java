package com.workmarket.domains.model.oauth;

import com.workmarket.api.internal.model.AccessKey;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;
import com.workmarket.utility.RandomUtilities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="oauthToken")
@Table(name="oauth_token")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType= DiscriminatorType.STRING)
@DiscriminatorValue("base")
@NamedQueries({
	@NamedQuery(name="oauthToken.byUserAndProvider", query="from oauthToken where user.id = :user_id and providerType.code = :provider_type_code order by id desc"),
	@NamedQuery(name="oauthToken.bySessionIdAndProvider", query="from oauthToken where sessionId = :session_id and providerType.code = :provider_type_code order by id asc")
})
public class OAuthToken extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private String requestToken;
	private String requestTokenSecret;
	private String accessToken;
	private String accessTokenSecret;
	private OAuthTokenProviderType providerType;

	private String sessionId;

	private User user;

	public OAuthToken(OAuthTokenProviderType providerType, String sessionId, User user) {
		this.providerType = providerType;
		this.sessionId = sessionId;
		this.user = user;
	}

	public OAuthToken() {
	}

	@Column(name="request_token", nullable=true)
	public String getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(String requestToken) {
		this.requestToken = requestToken;
	}

	@Column(name="request_token_secret", nullable=true)
	public String getRequestTokenSecret() {
		return requestTokenSecret;
	}

	public void setRequestTokenSecret(String requestTokenSecret) {
		this.requestTokenSecret = requestTokenSecret;
	}

	@Column(name="access_token", nullable=true)
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Column(name="access_token_secret", nullable=true)
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="provider_type_code", referencedColumnName="code", nullable=false)
	public OAuthTokenProviderType getProviderType() {
		return providerType;
	}

	public void setProviderType(OAuthTokenProviderType providerType) {
		this.providerType = providerType;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", referencedColumnName="id", updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name="session_id", nullable=true)
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	@Transient
	public static OAuthToken newRequestToken(final AccessKey key, final String accessToken) {
		OAuthToken token = new OAuthToken();
		token.setRequestToken(key.getToken());
		token.setRequestTokenSecret(key.getSecret());
		token.setUser(key.getUser());
		token.setAccessToken(accessToken);
		return token;
	}

	@Transient
	public static OAuthToken newRequestToken(AccessKey key) {
		return newRequestToken(key, RandomUtilities.generateAlphaNumericString(Constants.API_TOKEN_LENGTH));
	}
}

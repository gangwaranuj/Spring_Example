package com.workmarket.domains.model.integration.webhook;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="salesforceWebHookClient")
@DiscriminatorValue(AbstractWebHookClient.SALESFORCE)
@AuditChanges
public class SalesforceWebHookClient extends AbstractWebHookClient {
	private static final long serialVersionUID = -51938843867225462L;

	public static String ACCESS_TOKEN = "access_token";
	public static String AUTH_HEADER_NAME = "Authorization";
	public static String AUTH_HEADER_VALUE = "Bearer %s";

	public static final Long ACCESS_TOKEN_LIFESPAN = 28800L;

	private String refreshToken;
	private Boolean sandbox;

	@Column(name = "refresh_token")
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Column(name = "sandbox")
	public Boolean isSandbox() {
		return sandbox;
	}

	public void setSandbox(Boolean isSandbox) {
		this.sandbox = isSandbox;
	}
}

package com.workmarket.domains.model.oauth;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.LookupEntity;

@Entity(name="oauthTokenProviderType")
@Table(name="oauth_token_provider_type")
public class OAuthTokenProviderType extends LookupEntity {

	private static final long serialVersionUID = -7390231464382467504L;

	public static String LINKEDIN = "linkedin";
	public static String GOOGLE_CALENDAR = "calendar";
	public static String WM = "wm";

	public OAuthTokenProviderType() {}

	public OAuthTokenProviderType(String code) {
		super(code);
	}
	
	@Transient
	public static OAuthTokenProviderType newInstance(String code) {
		return new OAuthTokenProviderType(code);
	}
}

package com.workmarket.dao.oauth;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.oauth.OAuthToken;

public interface OAuthTokenDAO extends DAOInterface<OAuthToken> {
	OAuthToken findByAccessToken(String accessToken);

	OAuthToken findByUserAndProvider(Long userId, String providerTypeCode) ;
	OAuthToken findBySessionIdAndProvider(String sessionId, String providerTypeCode) ;
}
package com.workmarket.dao.google;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.google.RefreshToken;

public interface RefreshTokenDAO extends DAOInterface<RefreshToken> {

	public RefreshToken findByUserAndProvider(Long userId, String oAuthTokenProviderTypeCode);
}

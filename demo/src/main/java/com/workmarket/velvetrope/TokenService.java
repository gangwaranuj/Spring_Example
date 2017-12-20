package com.workmarket.velvetrope;

public interface TokenService {
	int tokenFor(Long companyId);
	void cacheToken(Long companyId, int token);
	void deleteTokenFor(Long companyId);
}

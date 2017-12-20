package com.workmarket.dao;

import com.workmarket.domains.model.BlacklistedEmail;

public interface BlacklistedEmailDAO extends DAOInterface<BlacklistedEmail> {
	Boolean isBlacklisted(String email);
	void deleteFromBlackList(String email);
}
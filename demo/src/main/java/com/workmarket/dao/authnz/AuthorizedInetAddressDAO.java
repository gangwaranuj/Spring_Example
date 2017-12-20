package com.workmarket.dao.authnz;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.authnz.AuthorizedInetAddress;

import java.util.Collection;

public interface AuthorizedInetAddressDAO extends DAOInterface<AuthorizedInetAddress> {
	Collection<AuthorizedInetAddress> findByCompany(String companyUuid);
	void setForCompany(String companyId, Collection<AuthorizedInetAddress> ips);
}

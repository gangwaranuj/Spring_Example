package com.workmarket.service.infra.business;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.authnz.AuthorizedInetAddress;
import com.workmarket.service.infra.security.RequestContext;

import java.util.Collection;
import java.util.List;

public interface AuthorizationService {
	/**
	 * Returns authorization context of the currently logged in user in relation to the userId
	 *
	 * @param user
	 * @return authorization context
	 */
	RequestContext getRequestContext(Long user) ;
	
	/**
	 * Get all authorization contexts given the current user and another user who is the owner of an entity.
	 * Note that this method does not take into account any entity-specific authorization; this should be used
	 * alongside any further entity-specific authorization.
	 * 
	 * @param currentUser
	 * @param entityOwner
	 * @param entityOwnerCompany
	 * @return
	 * @
	 */
	List<RequestContext> getEntityRequestContexts(User currentUser, User entityOwner, Company entityOwnerCompany);

	boolean authorizeByInetAddress(User user, String inetAddress);
	void setAuthorizedInetAddresses(Long companyUuid, Collection<AuthorizedInetAddress> ips);
	Collection<AuthorizedInetAddress> findAuthorizedInetAddressess(Long companyId);

	void setCompanyAuthByIp(Long companyId, boolean authorizeByInetAddress);

	boolean isCompanyAuthByInetAddress(Long companyId);
}

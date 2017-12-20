package com.workmarket.service.infra.business;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages;
import com.workmarket.auth.gen.Messages.CredentialValidationResponse;
import com.workmarket.auth.gen.Messages.GetEnforceIpRestrictionsResponse;
import com.workmarket.auth.gen.Messages.GetIpRestrictionsResponse;
import com.workmarket.auth.gen.Messages.Status;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.authnz.AuthorizedInetAddressDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.authnz.AuthorizedInetAddress;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import rx.functions.Func1;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class AuthorizationServiceImpl implements AuthorizationService {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

	@Autowired private SecurityContext securityContext;
	@Autowired private UserDAO userDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private AuthorizedInetAddressDAO authnzIpDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private LaneService laneService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private AuthTrialCommon trialCommon;
	@Autowired private AuthenticationClient authClient;

	@Override
	public RequestContext getRequestContext(final Long userId) {
		Assert.notNull(userId);
		Assert.notNull(securityContext.getCurrentUser());

		User currentUser = securityContext.getCurrentUser();

		if (currentUser.getId().equals(userId))
			return RequestContext.OWNER;

		if (authenticationService.userHasAclRole(currentUser.getId(), AclRole.ACL_MANAGER)
				|| authenticationService.userHasAclRole(currentUser.getId(), AclRole.ACL_ADMIN)) {
			User user = userDAO.get(userId);

			Assert.notNull(user);

			if (currentUser.getCompany().getId().equals(user.getCompany().getId())) {
				return RequestContext.ADMIN;
			}
		}

		return RequestContext.PUBLIC;
	}

	@Override
	public List<RequestContext> getEntityRequestContexts(User currentUser, User entityOwner, Company entityOwnerCompany) {
		List<RequestContext> contexts = Lists.newArrayList(RequestContext.PUBLIC);
		if (entityOwner.equals(currentUser)) {
			contexts.add(RequestContext.OWNER);
		} else if (entityOwner.getCompany().equals(currentUser.getCompany())) {
			contexts.add(RequestContext.COMPANY_OWNED);
		}

		LaneContext lane = laneService.getLaneContextForUserAndCompany(currentUser.getId(), entityOwnerCompany.getId());
		if (lane != null && lane.isInWorkerPool()) {
			contexts.add(RequestContext.WORKER_POOL);
		}

		return contexts;
	}

	@Override
	public boolean authorizeByInetAddress(final User user, final String inetAddress) {
		if (StringUtils.isEmpty(inetAddress)) {
			return true;
		}

		return authClient.validateUserIp(user.getEmail(), inetAddress, trialCommon.getApiContext())
			.map(new Func1<CredentialValidationResponse, Boolean>() {
				@Override
				public Boolean call(final CredentialValidationResponse credentialValidationResponse) {
					return credentialValidationResponse.getStatus() == Messages.ValidationStatus.OK;
				}
			}).toBlocking().single();
	}

	private List<String> inetAddressesToStrings(final Collection<AuthorizedInetAddress> addrs) {
		final ImmutableList.Builder<String> ipStrings = ImmutableList.builder();
		for (final AuthorizedInetAddress addr : addrs == null ? ImmutableList.<AuthorizedInetAddress>of() : addrs) {
			ipStrings.add(addr.getInetAddress());
		}
		return ipStrings.build();
	}

	@Override
	public void setAuthorizedInetAddresses(final Long companyId, final Collection<AuthorizedInetAddress> ips) {
		final String companyUuid = companyDAO.get(companyId).getUuid();

		final List<String> ipStrings = inetAddressesToStrings(ips);
		final Status result = authClient.setIpRestrictions(
			companyUuid, ipStrings, webRequestContextProvider.getRequestContext()).toBlocking().single();
		if (!result.getSuccess()) {
			throw new RuntimeException("Failed to save ip list" + result.getMessage());
		}
	}

	@Override
	public Collection<AuthorizedInetAddress> findAuthorizedInetAddressess(final Long companyId) {
		final String companyUuid = companyDAO.get(companyId).getUuid();

		return authClient.getIpRestrictions(companyUuid, webRequestContextProvider.getRequestContext())
			.map(new Func1<GetIpRestrictionsResponse, Collection<AuthorizedInetAddress>>() {
				@Override
				public Collection<AuthorizedInetAddress> call(final GetIpRestrictionsResponse addrs) {
					final ImmutableList.Builder<AuthorizedInetAddress> builder = ImmutableList.builder();
					for (final String addr : addrs.getIpRestrictionsList()) {
						final AuthorizedInetAddress newAddr = new AuthorizedInetAddress();
						newAddr.setInetAddress(addr);
						builder.add(newAddr);
					}
					return builder.build();
				}
			})
			.toBlocking().single();
	}

	@Override
	public void setCompanyAuthByIp(final Long companyId, final boolean authorizeByInetAddress) {
		final Company company = companyDAO.get(companyId);

		final com.workmarket.common.core.RequestContext context = webRequestContextProvider.getRequestContext();
		authClient.setEnforceIpRestrictions(company.getUuid(), authorizeByInetAddress, context)
			.toBlocking().single();
	}

	@Override
	public boolean isCompanyAuthByInetAddress(final Long companyId) {
		final Company company = companyDAO.get(companyId);

		return authClient.getEnforceIpRestrictions(company.getUuid(), trialCommon.getApiContext())
			.map(new Func1<GetEnforceIpRestrictionsResponse, Boolean>() {
					@Override
					public Boolean call(final GetEnforceIpRestrictionsResponse response) {
						return response.getEnforce();
					}
				}).toBlocking().single();
	}

}

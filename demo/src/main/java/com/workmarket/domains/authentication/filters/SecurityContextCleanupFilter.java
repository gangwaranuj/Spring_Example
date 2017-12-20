package com.workmarket.domains.authentication.filters;

import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.web.exceptions.AuthenticatedException;
import com.workmarket.web.exceptions.WebException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;

public class SecurityContextCleanupFilter extends GenericFilterBean {
	private static final Log logger = LogFactory.getLog(SecurityContextCleanupFilter.class);

	@Autowired private AuthenticationService authn;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private ExtendedUserDetailsService extendedUserDetailsService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		boolean refreshed = false;
		try {
			if (auth.getPrincipal() instanceof ExtendedUserDetails) {
				ExtendedUserDetails authenticatedUser = (ExtendedUserDetails) auth.getPrincipal();
				Long companyId = authenticatedUser.getCompanyId();
				Long userId = authenticatedUser.getId();
				Long retrievedOn = authenticatedUser.getRetrievedOn();

				List<Object> refreshes = redisAdapter.getMultiple(Lists.newArrayList(
						RedisFilters.refreshSecurityContextKeyForAll(),
						RedisFilters.refreshSecurityContextKeyForCompany(companyId),
						RedisFilters.refreshSecurityContextKeyForUser(userId)
				));

				Long latestRefresh = 0L;

				if (!refreshes.isEmpty()) {
					for (Object refresh : refreshes) {
						if (refresh == null) {
							continue;
						}

						try {
							Long refreshTimestamp = Long.valueOf(String.valueOf(refresh));

							if (refreshTimestamp > latestRefresh) {
								latestRefresh = refreshTimestamp;
							}
						} catch(NumberFormatException e) {
							logger.error("Error refreshing security context for user " + userId + ". Could not convert " + refresh + " to a Long", e);
						}
					}
				}

				try {
					if (retrievedOn == null || latestRefresh > retrievedOn) {
						UserDetails savedUser = extendedUserDetailsService.loadUserByUsername(authenticatedUser.getUsername());
						PropertyUtils.copyProperties(authenticatedUser, savedUser);

						if (!authenticatedUser.isMasquerading() && (authenticatedUser.isSuspended() || authenticatedUser.isDeactivated())) {
							SecurityContextHolder.getContext().setAuthentication(null);
						}
						refreshed = true;
					}
				} catch (UsernameNotFoundException e) {
					// They have changed and confirmed a new email (username) during this session
					// log them out so they have to log in with a new username
					SecurityContextHolder.getContext().setAuthentication(null);
				} catch (Exception e) {
					logger.error("Error refreshing user details for user id " + userId + " with username " + authenticatedUser.getUsername(), e);
				}
			}

			chain.doFilter(req, res);
		}
		catch (ServletException e) {
			if (!(e.getCause() instanceof WebException) && auth.getPrincipal() instanceof ExtendedUserDetails) {
				ExtendedUserDetails extendedUserDetails = (ExtendedUserDetails) auth.getPrincipal();

				throw new AuthenticatedException(e, extendedUserDetails.getEmail(), extendedUserDetails.getUserNumber(),
						extendedUserDetails.getCompanyEffectiveName(), String.valueOf(extendedUserDetails.getCompanyId()));
			} else {
				throw e;
			}
		}
		finally {
			if (refreshed) {
				authn.clearCurrentUser();
			}
		}
	}
}

package com.workmarket.domains.authentication.services;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.stereotype.Component;

@Component("securityContextFacade")
public class SecurityContextFacadeImpl implements SecurityContextFacade {
	@Override
	public SecurityContext getSecurityContext() {
		return SecurityContextHolder.getContext();
	}

	@Override
	public ExtendedUserDetails getCurrentUser() {
		Authentication auth = getSecurityContext().getAuthentication();

		if (auth == null) return null;

		Object principal = auth.getPrincipal();
		if (principal instanceof ExtendedUserDetails) {
			ExtendedUserDetails extendedUserDetails = (ExtendedUserDetails) principal;

			// transfer masquerade flag and user to ExtendedUserDetails
			for (GrantedAuthority authority : auth.getAuthorities()) {
				if (authority instanceof SwitchUserGrantedAuthority) {
					extendedUserDetails.setMasqueradeUser((ExtendedUserDetails)
							((SwitchUserGrantedAuthority) authority).getSource().getPrincipal());
					break;
				}
			}
			return extendedUserDetails;
		}
		return null;
	}
}

package com.workmarket.domains.velvetrope.service;

import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.velvetrope.AuthenticatedGuestService;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtendedUserGuestService implements AuthenticatedGuestService<ExtendedUserDetails> {
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private TokenService tokenService;

	@Override
	public Guest getGuest() {
		int token = WebGuest.EMPTY_TOKEN;
		ExtendedUserDetails user = getCurrentUser();

		if (user != null) { token = tokenService.tokenFor(user.getCompanyId()); }

		return makeGuest(user, token);
	}

	@Override
	public Guest makeGuest(ExtendedUserDetails user, int token) {
		return new WebGuest(user, token);
	}

	private ExtendedUserDetails getCurrentUser() {
		return securityContextFacade == null ? null : securityContextFacade.getCurrentUser();
	}
}

package com.workmarket.domains.velvetrope.service;

import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.TokenService;
import com.workmarket.velvetrope.UnauthenticatedGuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimpleGuestService implements UnauthenticatedGuestService<Guest> {

	@Autowired private TokenService tokenService;

	@Override
	public Guest getGuest(Guest guest) {
		int token = tokenService.tokenFor(guest.getCompanyId());
		guest.setToken(token);
		return guest;
	}
}

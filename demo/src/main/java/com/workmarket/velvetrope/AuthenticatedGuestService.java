package com.workmarket.velvetrope;

public interface AuthenticatedGuestService<T> extends GuestService {
	Guest getGuest();
	Guest makeGuest(T user, int token);
}

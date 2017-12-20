package com.workmarket.velvetrope;

public abstract class AbstractGuest<T> implements Guest<T> {
	public static final int EMPTY_TOKEN = 0;
	private final T user;
	private int token;

	public AbstractGuest(T user, int token) {
		this.user = user;
		this.token = token;
	}

	@Override
	public boolean canEnter(Venue venue) {
		return (venue.mask() & this.token) == venue.mask();
	}

	@Override
	public void setToken(int token) {
		this.token = token;
	}

	@Override
	public T getUser() {
		return user;
	}
}

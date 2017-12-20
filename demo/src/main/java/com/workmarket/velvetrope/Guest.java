package com.workmarket.velvetrope;

public interface Guest<T> {
	boolean canEnter(Venue venue);
	void setToken(int token);
	T getUser();
	long getId();
	Long getCompanyId();
}

package com.workmarket.velvetrope;

public interface Doorman<T extends Rope> {
	void welcome(Guest guest, T rope);
}

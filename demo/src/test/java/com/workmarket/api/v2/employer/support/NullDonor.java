package com.workmarket.api.v2.employer.support;

import com.natpryce.makeiteasy.Donor;

public class NullDonor<T> implements Donor<T> {
	@Override public T value() {
		return null;
	}
}

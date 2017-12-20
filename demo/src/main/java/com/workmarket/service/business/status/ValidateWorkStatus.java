package com.workmarket.service.business.status;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.service.status.ResponseStatus;

import java.util.Collection;

/**
 * User: micah
 * Date: 8/25/13
 * Time: 6:12 PM
 */
public enum ValidateWorkStatus implements ResponseStatus {
	FAILURE,
	SUCCESS;

	private static Collection<ValidateWorkStatus> FAILURE_STATES = ImmutableList.of(
			FAILURE
	);

	@Override
	public boolean isSuccessful() {
		return !FAILURE_STATES.contains(this);
	}

	@Override
	public boolean isFailure() {
		return !isSuccessful();
	}
}

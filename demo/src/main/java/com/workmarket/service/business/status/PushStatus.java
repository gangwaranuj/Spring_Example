package com.workmarket.service.business.status;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.service.status.ResponseStatus;

import java.util.Collection;

/**
 * User: andrew
 * Date: 12/9/13
 */
public enum PushStatus implements ResponseStatus {

	FAILURE,
	INVALID_DEVICE,
	SUCCESS,
	NONE;

	private static final Collection<PushStatus> FAILURE_STATES = ImmutableList.of(
			FAILURE,
			INVALID_DEVICE,
			NONE);

	@Override
	public boolean isSuccessful() {
		return !FAILURE_STATES.contains(this);
	}

	@Override
	public boolean isFailure() {
		return !isSuccessful();
	}
}

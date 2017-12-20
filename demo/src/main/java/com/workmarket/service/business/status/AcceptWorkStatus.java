package com.workmarket.service.business.status;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.service.status.ResponseStatus;

import java.util.Collection;

/**
 * Created by nick on 4/4/13 3:47 PM
 */

public enum AcceptWorkStatus implements ResponseStatus {

	FAILURE,
	NOT_SENT_STATUS,
	INVALID_RESOURCE,
	SUCCESS,
	NONE;

	private static Collection<AcceptWorkStatus> FAILURE_STATES = ImmutableList.of(
			FAILURE,
			NOT_SENT_STATUS,
			INVALID_RESOURCE,
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

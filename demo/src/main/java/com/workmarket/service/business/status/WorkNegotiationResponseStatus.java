package com.workmarket.service.business.status;

import com.google.common.collect.Lists;
import com.workmarket.common.service.status.ResponseStatus;

import java.util.Collection;

public enum WorkNegotiationResponseStatus implements ResponseStatus {

	FAILURE,
	SUCCESS,
	NONE,
	DUPLICATES;

	private static Collection<WorkNegotiationResponseStatus> FAILURE_STATES = Lists.newArrayList(
			FAILURE,
			NONE,
			DUPLICATES);

	@Override
	public boolean isSuccessful() {
		return !FAILURE_STATES.contains(this);
	}

	@Override
	public boolean isFailure() {
		return !isSuccessful();
	}
}

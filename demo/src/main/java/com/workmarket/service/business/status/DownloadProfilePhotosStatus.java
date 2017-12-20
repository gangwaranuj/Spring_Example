package com.workmarket.service.business.status;

import com.google.common.collect.Lists;
import com.workmarket.common.service.status.ResponseStatus;

import java.util.Collection;

/**
 * Statuses for Download Profile Photos System
 */

public enum DownloadProfilePhotosStatus implements ResponseStatus {

	FAILURE,
	SUCCESS,
	NONE;

	private static Collection<DownloadProfilePhotosStatus> FAILURE_STATES = Lists.newArrayList(
			FAILURE,
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

package com.workmarket.common.service.status;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * Created by nick on 2013-04-15 8:05 AM
 */
public class BaseStatus extends org.apache.commons.lang.enums.Enum implements ResponseStatus {

	public static BaseStatus SUCCESS = new BaseStatus("Success");
	public static BaseStatus FAILURE = new BaseStatus("Failure");
	public static BaseStatus NONE = new BaseStatus("None");

	private static final Collection<BaseStatus> FAILURE_STATES = ImmutableList.of(FAILURE, NONE);

	protected BaseStatus(String name) {
		super(name);
	}

	@Override
	public boolean isSuccessful() {
		return !FAILURE_STATES.contains(this);
	}

	@Override
	public boolean isFailure() {
		return !isSuccessful();
	}
}
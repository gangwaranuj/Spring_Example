package com.workmarket.service.business.status;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.status.ResponseStatus;

import java.util.Collection;

/**
 * Created by nick on 4/4/13 3:47 PM
 */

public class CloseWorkStatus extends BaseStatus implements ResponseStatus {

	private static final long serialVersionUID = -2451995790522109696L;

	public static CloseWorkStatus CLOSED = new CloseWorkStatus("Closed");
	public static CloseWorkStatus CLOSED_BY_EMPLOYEE = new CloseWorkStatus("Closed by Employee");
	public static CloseWorkStatus CLOSED_IMMEDIATELY = new CloseWorkStatus("Closed Immediately");
	public static CloseWorkStatus CLOSED_AND_PAID = new CloseWorkStatus("Closed and Paid");
	public static CloseWorkStatus CLOSED_AND_AUTOPAID = new CloseWorkStatus("Closed and Auto-Paid");
	public static CloseWorkStatus INSUFFICIENT_FUNDS = new CloseWorkStatus("Insufficient Funds"); // TODO: this can be exported and used elsewhere
	public static CloseWorkStatus GENERAL_ERROR = new CloseWorkStatus("General Error");

	private static final Collection<BaseStatus> FAILURE_STATES = ImmutableList.of(FAILURE, NONE, INSUFFICIENT_FUNDS);

	protected CloseWorkStatus(String name) {
		super(name);
	}

	@Override
	public boolean isSuccessful() {
		return !this.FAILURE_STATES.contains(this);
	}

	@Override
	public boolean isFailure() {
		return !this.isSuccessful();
	}
}

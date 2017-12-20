package com.workmarket.service.business.wrapper;

import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.service.business.status.AcceptWorkStatus;
import com.workmarket.service.business.status.PushStatus;

/**
 * User: andrew
 * Date: 12/9/13
 */
public class PushResponse extends MessageResponse {

	public PushResponse() {
		super();
		this.status = PushStatus.NONE;
	}

	public PushResponse(PushStatus status) {
		super(status);
	}

	public PushResponse(PushStatus status, String message) {
		super(status, message);
	}

	public static PushResponse invalidDevice() {
		return new PushResponse(PushStatus.INVALID_DEVICE);
	}

	public static PushResponse success() {
		return new PushResponse(PushStatus.SUCCESS);
	}

	public static PushResponse fail() {
		return new PushResponse(PushStatus.FAILURE);
	}
}

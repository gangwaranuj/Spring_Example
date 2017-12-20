package com.workmarket.service.business.wrapper;

import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.service.business.status.ValidateWorkStatus;

/**
 * User: micah
 * Date: 8/25/13
 * Time: 6:11 PM
 */
public class ValidateWorkResponse extends MessageResponse {
	public ValidateWorkResponse() {
		super();
	}

	public ValidateWorkResponse(ValidateWorkStatus status) {
		super(status);
	}

	public ValidateWorkResponse(ValidateWorkStatus status, String message) {
		super(status, message);
	}

	public static ValidateWorkResponse success() {
		return new ValidateWorkResponse(ValidateWorkStatus.SUCCESS);
	}

	public static ValidateWorkResponse fail() {
		return new ValidateWorkResponse(ValidateWorkStatus.FAILURE);
	}
}

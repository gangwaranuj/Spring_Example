package com.workmarket.service.infra.communication;

import com.workmarket.notification.push.vo.PushNotifyResponse;
import com.workmarket.service.business.status.PushStatus;
import com.workmarket.service.business.wrapper.PushResponse;

public class PushResponseConverter {
	public static PushResponse convertFromPushNotifyResponse(final PushNotifyResponse response) {
		final PushStatus status;

		switch (response.getStatus()) {
			case FAILURE:
				status = PushStatus.FAILURE;
				break;
			case SUCCESS:
				status = PushStatus.SUCCESS;
				break;
			case INVALID_DEVICE:
				status = PushStatus.INVALID_DEVICE;
				break;
			default:
				status = PushStatus.NONE;
		}

		return new PushResponse(status, response.getMessage());
	}
}

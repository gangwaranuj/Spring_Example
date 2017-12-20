package com.workmarket.service.infra;

import com.workmarket.service.business.dto.PushDTO;
import com.workmarket.service.business.wrapper.PushResponse;

/**
 * User: andrew
 * Date: 11/19/13
 */
public interface PushService {
	PushResponse sendPush(PushDTO pushDTO);
	PushResponse sendPush(final long toUserId, final String message, final String regid, final String type);
}

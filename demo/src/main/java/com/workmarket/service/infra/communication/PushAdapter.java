package com.workmarket.service.infra.communication;

import com.workmarket.service.business.wrapper.PushResponse;

import java.util.Date;
import java.util.Map;

/**
 * User: andrew
 * Date: 11/19/13
 */
public interface PushAdapter {

	PushResponse sendAndroidPush(String regid, String message, String action);

	PushResponse sendIosPush(String regid, String message, String action);

	Map<String, Date> removeUnusedIos();
}

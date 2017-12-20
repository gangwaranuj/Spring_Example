package com.workmarket.service.infra;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.push.PushTemplateParser;
import com.workmarket.domains.model.notification.DeviceType;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.PushDTO;
import com.workmarket.service.business.status.PushStatus;
import com.workmarket.service.business.wrapper.PushResponse;
import com.workmarket.service.infra.communication.PushAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * User: andrew
 * Date: 11/19/13
 */
@Service
public class PushServiceImpl implements PushService {
	// null registration IDs are returned from Apple as "(null)"
	private static final String IOS_NULL_REGID = "(null)";

	@Autowired private PushAdapter pushAdapter;
	@Autowired private UserService userService;
	@Autowired private PushTemplateParser pushTemplateParser;
	@Autowired private MetricRegistry metricRegistry;

	private WMMetricRegistryFacade wmMetricRegistryFacade;
	private Meter iosSendSuccessMeter;
	private Meter iosSendFailureMeter;
	private Meter androidSendSuccessMeter;
	private Meter androidSendFailureMeter;

	@PostConstruct
	private void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "push-service");
		iosSendSuccessMeter = wmMetricRegistryFacade.meter("ios.send.success");
		iosSendFailureMeter = wmMetricRegistryFacade.meter("ios.send.failure");
		androidSendSuccessMeter = wmMetricRegistryFacade.meter("android.send.success");
		androidSendFailureMeter = wmMetricRegistryFacade.meter("android.send.failure");
	}

	private static final Log logger = LogFactory.getLog(PushServiceImpl.class);

	@Override
	public PushResponse sendPush(PushDTO pushDTO) {
		return sendPush(pushDTO.getToUserId(), pushDTO.getMessage(), pushDTO.getRegid(), pushDTO.getType());
	}

	@Override
	public PushResponse sendPush(final long toUserId, final String message, final String regid, final String type) {
		logger.debug("[Push] start sendPush");

		final String action = pushTemplateParser.parseAction(message);
		final String msg = pushTemplateParser.parseMessage(message);

		if (!isParseable(action, msg, regid)) {
			logger.error(
				String.format("Push template was not parsed properly action: %s message: %s, for message: %s, %s ",
				action, msg, message, regid));
			return PushResponse.fail();
		}

		final PushResponse response;
		if (type.equals(DeviceType.ANDROID.getCode())) {
			response = pushAdapter.sendAndroidPush(regid, msg, action);
		} else if (type.equals(DeviceType.IOS.getCode())) {
			response = pushAdapter.sendIosPush(regid, msg, action);
		} else {
			response = new PushResponse();
		}

		if (response.getStatus().equals(PushStatus.INVALID_DEVICE)) {
			logger.warn(String.format("Device %s not found. Removing association with user id %s", regid, toUserId));
			userService.removeDevice(toUserId, regid);
		}

		// Track outcome
		if (response.isSuccessful()) {
			if (type.equals(DeviceType.ANDROID.getCode())) {
				androidSendSuccessMeter.mark();
			} else {
				iosSendSuccessMeter.mark();
			}
		} else {
			if (type.equals(DeviceType.ANDROID.getCode())) {
				androidSendFailureMeter.mark();
			} else {
				iosSendFailureMeter.mark();
			}
		}

		return response;
	}

	private boolean isParseable(final String action, final String msg, final String regid) {
		return !StringUtils.isBlank(action) && !StringUtils.isBlank(msg) && isValidRegistrationId(regid);
	}

	private boolean isValidRegistrationId(final String regid) {
		return !StringUtils.isBlank(regid) && !IOS_NULL_REGID.equals(regid);
	}
}

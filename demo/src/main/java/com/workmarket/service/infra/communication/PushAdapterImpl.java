package com.workmarket.service.infra.communication;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.dao.notification.UserDeviceAssociationDAO;
import com.workmarket.domains.model.notification.UserDeviceAssociation;
import com.workmarket.notification.NotificationClient;
import com.workmarket.notification.push.vo.PushNotifyDeviceType;
import com.workmarket.service.business.wrapper.PushResponse;
import com.workmarket.service.web.WebRequestContextProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * User: andrew
 * Date: 11/19/13
 */
@Service
public class PushAdapterImpl implements PushAdapter {
	private static final String APPLE_CERTIFICATE_NAME = "apple_push_cert.p12";

	private static final Logger logger = LoggerFactory.getLogger(PushAdapterImpl.class);

	@Value("${app.isTestEnvironment}")
	private String testEnvironment;
	@Value("${push.adapter.appleCertPassword}")
	private String appleCertPassword;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	private ApnsService apnsService = null;
	private final NotificationClient client;
	private final Meter pushSendAndroidMeter;
	private final Meter pushSendIosMeter;
	private final UserDeviceAssociationDAO userDeviceAssociationDAO;

	@Autowired
	PushAdapterImpl(final NotificationClient client,
					final MetricRegistry metricRegistry,
					final UserDeviceAssociationDAO userDeviceAssociationDAO) {
		this.client = client;
		this.userDeviceAssociationDAO = userDeviceAssociationDAO;
		final WMMetricRegistryFacade wmMetricRegistryFacade =
			new WMMetricRegistryFacade(metricRegistry, "push-service");
		this.pushSendAndroidMeter = wmMetricRegistryFacade.meter("send.android");
		this.pushSendIosMeter = wmMetricRegistryFacade.meter("send.ios");
	}

	@Override
	public PushResponse sendAndroidPush(final String regid, final String message, final String action) {
		return send(PushNotifyDeviceType.ANDROID, regid, message, action);
	}

	@Override
	public PushResponse sendIosPush(final String token, final String message, final String action) {
		return send(PushNotifyDeviceType.IOS, token, message, action);
	}

	@Override
	public Map<String, Date> removeUnusedIos() {
		final Map<String, Date> inactiveDevices = getApnsService().getInactiveDevices();
		for (final String deviceToken : inactiveDevices.keySet()) {
			final UserDeviceAssociation deviceAssociation = userDeviceAssociationDAO.findByDeviceUID(deviceToken);
			deviceAssociation.setDeleted(true);
			userDeviceAssociationDAO.saveOrUpdate(deviceAssociation);
		}
		return inactiveDevices;
	}

	private ApnsService getApnsService() {
		if (apnsService == null) {
			final InputStream certStream = this.getClass().getClassLoader().getResourceAsStream(APPLE_CERTIFICATE_NAME);
			apnsService = APNS.newService()
				.withCert(certStream, this.appleCertPassword) //needs to have a password, so this is the best to do
				.withAppleDestination(!Boolean.valueOf(this.testEnvironment))
				.build();
		}

		return apnsService;
	}

	private PushResponse send(final PushNotifyDeviceType type, final String regId, final String message,
							  final String action) {
		try {
			logger.info("sending push notification off to notification microservice");
			final PushResponse response = PushResponseConverter.convertFromPushNotifyResponse(
					client.sendPush(type, regId, message, action, webRequestContextProvider.getRequestContext())
							.toBlocking().single());
			logger.info("send to microservice succeeded? [true]");
			if (PushNotifyDeviceType.ANDROID.equals(type)) {
				pushSendAndroidMeter.mark();
			} else if (PushNotifyDeviceType.IOS.equals(type)) {
				pushSendIosMeter.mark();
			}

			return response;
		} catch (final Exception e) {
			logger.info("send to microservice succeeded? [false]");
			logger.error("error sending android push", e);
			return PushResponse.fail();
		}
	}
}

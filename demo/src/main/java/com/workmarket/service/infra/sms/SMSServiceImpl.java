package com.workmarket.service.infra.sms;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.api.exception.BadRequestException;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.logging.NRTrace;
import com.workmarket.notification.NotificationClient;
import com.workmarket.notification.sms.vo.SmsNotifyResponse;
import com.workmarket.service.business.dto.SMSDTO;
import com.workmarket.service.web.WebRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class SMSServiceImpl implements SMSService {
	private static final Logger logger = LoggerFactory.getLogger(SMSServiceImpl.class);
	private final Meter smsSendMeter;
	private final NotificationClient notificationClient;
	private final WebRequestContextProvider webRequestContextProvider;

	@Autowired
	SMSServiceImpl(final MetricRegistry metricRegistry,
	               final NotificationClient notificationClient,
	               final WebRequestContextProvider webRequestContextProvider) {
		this.notificationClient = notificationClient;
		this.webRequestContextProvider = webRequestContextProvider;

		final WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "sms-service");
		this.smsSendMeter = wmMetricRegistryFacade.meter("send");
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/Message/Sms")
	public void sendSMS(final SMSDTO dto) throws Exception {
		Assert.notNull(dto);
		sendSMS(dto.getToNumber(), dto.getMsg());
	}

	@Override
	public SmsNotifyResponse sendSMS(final String toNumber, final String message) {
		logger.info("sending SMS notification off to notification microservice");
		// should propagate from the notifications service this way
		if (isBlank(toNumber)) {
			throw new BadRequestException("toNumber must not be blank");
		}
		final SmsNotifyResponse response = notificationClient
				.sendSms(toNumber, message, webRequestContextProvider.getRequestContext())
				.toBlocking()
				.single();
		logger.info("send to microservice succeeded? [true]");
		smsSendMeter.mark();
		return response;
	}
}

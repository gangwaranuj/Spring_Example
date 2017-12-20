package com.workmarket.api.v2.employer.uploads.services;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.api.v2.employer.uploads.async.UploadMessageCreator;
import com.workmarket.api.v2.employer.uploads.events.UploadEvent;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.service.web.WebRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("uploadsEventService")
public class EventServiceImpl implements EventService {

	private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Qualifier("uploadTemplate")
	@Autowired private JmsTemplate uploadTemplate;
	@Autowired private MetricRegistry metricRegistry;
	private Meter sendMeter;
	private Meter sendAttemptMeter;
	private Meter sendFailMeter;

	@PostConstruct
	private void init() {
		final WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "jms");
		sendAttemptMeter = wmMetricRegistryFacade.meter("send.attempt");
		sendMeter = wmMetricRegistryFacade.meter("send.sync.success");
		sendFailMeter = wmMetricRegistryFacade.meter("send.fail");
	}

	@Override
	public void emit(UploadEvent event) {
		sendAttemptMeter.mark();
		webRequestContextProvider.inject(event);
		try {
			logger.debug("Emitting UploadEvent[" + event.getClass().getName() + "]: " + event);
			uploadTemplate.send(new UploadMessageCreator(event));
			sendMeter.mark();
		} catch (JmsException ex) {
			logger.debug("Error Emitting UploadEvent: " + event);
			sendFailMeter.mark();
		}
	}
}

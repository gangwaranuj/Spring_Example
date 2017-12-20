package com.workmarket.api.v2.employer.uploads.async;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.api.v2.employer.uploads.events.UploadEvent;
import com.workmarket.api.v2.employer.uploads.visitors.Visitor;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Service
public class UploadListener implements MessageListener {
	private static final Logger logger = LoggerFactory.getLogger(UploadListener.class);

	@Qualifier("listenerVisitor")
	@Autowired private Visitor visitor;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	private Meter consumeMeter;

	@PostConstruct
	private void init() {
		final WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "jms.listener.upload");
		consumeMeter = wmMetricRegistryFacade.meter("consume");
	}
	
	@Override
	public void onMessage(Message message) {
		consumeMeter.mark();
		ObjectMessage objectMessage = (ObjectMessage) message;

		logger.debug("Received message: " + objectMessage.getClass().getName() + " -> " + objectMessage);

		try {
			UploadEvent uploadEvent = (UploadEvent) objectMessage.getObject();
			authenticationService.setCurrentUser(uploadEvent.getUserId());
			webRequestContextProvider.extract(uploadEvent);
			uploadEvent.accept(visitor);
		} catch (JMSException e) {
			logger.error("oopsie processing message", e);
			throw new RuntimeException(e);
		}
	}
}

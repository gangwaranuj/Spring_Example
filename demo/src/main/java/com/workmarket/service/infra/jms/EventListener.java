package com.workmarket.service.infra.jms;


import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Service
public class EventListener implements MessageListener {

	private static final Log logger = LogFactory.getLog(EventListener.class);
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private EventRouter eventRouter;
	@Autowired private MetricRegistry metricRegistry;
	private Meter consumeMeter;
	
	@PostConstruct
	private void init() {
		final WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "jms.listener.event");
		consumeMeter = wmMetricRegistryFacade.meter("consume");
	}
	
	public void onMessage(Message message) {
		consumeMeter.mark();
		if (message instanceof ObjectMessage) {
			try {
				ObjectMessage objectMessage = (ObjectMessage) message;
				Object object = objectMessage.getObject();
				Event event = null;

				webRequestContextProvider.extract(object);

				if (object instanceof Event) {
					event = (Event)object;
					logger.info(String.format("[onMessage] BEFORE Thread id: %s . Message: %s", Thread.currentThread().getId(), event.toString()));
				}

				eventRouter.onEvent(object);

				if (event != null) {
					logger.info(String.format("[onMessage] AFTER EXECUTION Thread id: %s . Message: %s", Thread.currentThread().getId(), event.toString()));
				}

			} catch (Exception ex) {
				logger.info("jms message error: " + message.toString() + ", message=" + ex.getMessage());
			}
		} else {
			logger.error("Message was not of type ObjectMessage");
			throw new IllegalArgumentException("Message must be of type ObjectMessage");
		}
	}
}
package com.workmarket.service.business.queue.integration;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.dao.integration.IntegrationEventTypeDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.util.WebHookInvocationBuilder;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.id.IdGenerator;
import com.workmarket.integration.webhook.WebHookDispatchField;
import com.workmarket.service.SpringInitializedService;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;
import com.workmarket.service.business.integration.event.IntegrationEvent;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.integration.event.IntegrationListenerServiceImpl;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;

import com.workmarket.webhook.relay.gen.Message;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.functions.Func1;

import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by nick on 2012-12-29 11:05 AM
 */
@Service
public class IntegrationEventServiceImpl extends SpringInitializedService implements IntegrationEventService, Runnable {


	@Autowired @Qualifier("integrationMessageTemplate") JmsTemplate integrationMessageTemplate;
	@Autowired AuthenticationService authenticationService;
	@Autowired IntegrationEventTypeDAO integrationEventTypeDAO;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired WebRequestContextProvider webRequestContextProvider;
	@Autowired IdGenerator idGenerator;
	@Resource(name = "integrationListener") MessageListener integrationListener;
	@Autowired FeatureEntitlementService featureEntitlementService;

	private WMMetricRegistryFacade metricFacade;

	private static final ExecutorService runner = Executors.newSingleThreadExecutor();
	private static final AtomicBoolean isRunning = new AtomicBoolean(false);
	private final DelayQueue<IntegrationEvent> queue;
	private static final Logger logger = LoggerFactory.getLogger(IntegrationEventServiceImpl.class);

	public IntegrationEventServiceImpl() {
		this.queue = new DelayQueue<>();
	}

	public void sendEvent(final IntegrationEvent event) {
		webRequestContextProvider.inject(event);

		final Observable<FeatureToggleAndStatus> toggle =
			featureEntitlementService.getFeatureToggleForCurrentUser("webhook.relay");

		try {
			toggle.map(new Func1<FeatureToggleAndStatus, Boolean>() {
				@Override
				public Boolean call(FeatureToggleAndStatus toggleResult) {
					if (toggleResult.getStatus().getSuccess()
						&& toggleResult.getFeatureToggle() != null
						&& Boolean.valueOf(toggleResult.getFeatureToggle().getValue())) { // send via Webhook Relay Service
						try {
							final ActiveMQObjectMessage message = new ActiveMQObjectMessage();
							message.setObject(event);
							metricFacade.meter("produce.event." + event.getEventType().getCode()).mark();
							integrationListener.onMessage(message);
						} catch (JMSException e) {
							logger.error("Error serializing ActiveMQObjectMessage", e);
							throw new RuntimeException("Error serializing activeMQObjectMessage");
						}
					} else {
						queue.offer(event);
					}
					return true;
				}
			}).toBlocking().single();
		}
		catch(Exception e) {
			queue.offer(event);
		}
	}

	@PostConstruct
	void init() {
		metricFacade = new WMMetricRegistryFacade(metricRegistry, "webhook");
	}

	@Override
	public void initialize() {
		checkNotNull(this.queue);
		if (!isRunning.get()) {
			isRunning.set(true);
			runner.execute(this);
		}
	}

	@Override
	public void run() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		IntegrationEvent data = null;
		try {
			while (true) {
				data = queue.take();
				try {
					metricFacade.meter("produce").mark();
					data.setCurrentTimeInMillis(System.currentTimeMillis());
					this.integrationMessageTemplate.convertAndSend(data);
					logger.info("[integration] sent delayed event for " + data.getEventType().getCode());
					metricFacade.meter("produce.event." + data.getEventType().getCode()).mark();
				} catch (Exception e) {
					if (data != null && data.getEventType() != null) {
						metricFacade.meter("produce.error.event." + data.getEventType().getCode()).mark();
					} else {
						metricFacade.meter("produce.error.event.unknown").mark();
					}

					logger.error("Event process failed for " + data, e);
				}
			}
		} catch (InterruptedException e1) {
			if (data != null && data.getEventType() != null) {
				metricFacade.meter("produce.error.event." + data.getEventType().getCode()).mark();
			} else {
				metricFacade.meter("produce.error.event.unknown").mark();
			}
			logger.error(String.valueOf(e1));
		}
	}


	@Override
	public List<IntegrationEventType> findIntegrationEventTypes() {
		return integrationEventTypeDAO.findIntegrationEventTypes();
	}
}


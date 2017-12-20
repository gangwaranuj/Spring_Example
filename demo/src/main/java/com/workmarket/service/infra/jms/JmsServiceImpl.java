package com.workmarket.service.infra.jms;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.AbstractWorkNotificationTemplate;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.service.business.dto.NotificationDTO;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.ScheduledEvent;
import com.workmarket.service.business.event.search.IndexerEvent;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.DateUtilities;
import org.apache.activemq.ScheduledMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Calendar;
import java.util.UUID;

@Service
public class JmsServiceImpl implements JmsService {

	private static final Log logger = LogFactory.getLog(JmsServiceImpl.class);
	private static final String COMMAND_GROUP = "jms";
	private static final String COMMAND_KEY = "send";

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private MetricRegistry metricRegistry;
	private Meter sendAttemptMeter;
	private Meter sendSyncMeter;
	private Meter sendFailMeter;
	private Meter sendAsyncMeter;
	
	@PostConstruct
	private void init() {
		final WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "jms");
		sendAttemptMeter = wmMetricRegistryFacade.meter("send.attempt");
		sendSyncMeter = wmMetricRegistryFacade.meter("send.sync.success");
		sendAsyncMeter = wmMetricRegistryFacade.meter("send.async.success");
		sendFailMeter = wmMetricRegistryFacade.meter("send.fail");
	}

	@Autowired @Qualifier("emailMessageTemplate")
	private JmsTemplate emailMessageTemplate;

	@Autowired @Qualifier("eventMessageTemplate")
	private JmsTemplate eventMessageTemplate;

	@Autowired @Qualifier("batchTemplate")
	private JmsTemplate batchTemplate;

	@Autowired @Qualifier("reindexEventMessageTemplate")
	private JmsTemplate reindexEventMessageTemplate;

	@Autowired @Qualifier("workEmailMessageTemplate")
	private JmsTemplate workEmailMessageTemplate;

	@Override
	public void sendMessage(final NotificationDTO notificationDTO) {
		webRequestContextProvider.inject(notificationDTO);
		sendMessage(notificationDTO, null);
	}

	@Override
	public void sendMessage(final NotificationDTO notificationDTO,
							final Calendar scheduleDate) {
		webRequestContextProvider.inject(notificationDTO);
		try {
			sendSync(emailMessageTemplate, new MessageCreator() {

				@Override
				public Message createMessage(Session session)
						throws JMSException {
					Message message = session
							.createObjectMessage(notificationDTO);

					if (scheduleDate != null) {

						// if this message needs to be sent in the future
						// find the delay in milliseconds 
						Long timeDelta = DateUtilities
								.getDifferenceInMillisFromNow(scheduleDate);

						if (timeDelta > 0) {
							logger.info(String.format("Scheduling NotificationDTO Message AMQ job [%s] for: %s",
									notificationDTO.getClass().getName(),
									DateUtilities.getISO8601(scheduleDate)));
							message.setLongProperty(
									ScheduledMessage.AMQ_SCHEDULED_DELAY, timeDelta);
						}
					}
					return message;
				}
			});

		} catch (Exception e) {
			// that's it. errors sending JMS messages should no longer prevent a
			// web request from completing.
			if (notificationDTO.getNotificationType() != null) {
				logger.error("Send Delayed Message - JMS Error NotificationDTO.getType() "
						+ notificationDTO.getNotificationType().getCode(), e);
			} else {
				logger.error("Send Delayed Message - JMS Error NotificationDTO.getType() -- notificationType is null", e);
			}
			sendFailMeter.mark();
		}
	}

	@Override
	public void sendMessage(final NotificationTemplate notificationTemplate, final Calendar scheduleDate) {
		webRequestContextProvider.inject(notificationTemplate);

		try {
			Assert.notNull(notificationTemplate);
			MessageCreator messageCreator = new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					Message message = session.createObjectMessage(notificationTemplate);

					if (scheduleDate != null) {

						// if this message needs to be sent in the future
						// find the delay in milliseconds
						long timeDelta = DateUtilities.getDifferenceInMillisFromNow(scheduleDate);
						if (timeDelta > 0) {
							logger.info(String.format("Scheduling NotificationTemplate Message AMQ job [%s] for: %s",
									notificationTemplate.getClass().getName(),
									DateUtilities.getISO8601(scheduleDate)));
							message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, timeDelta);
						}
					}
					return message;
				}
			};	

			if (notificationTemplate instanceof AbstractWorkNotificationTemplate) {
				sendSync(workEmailMessageTemplate, messageCreator);
			} else {
				sendSync(emailMessageTemplate, messageCreator);
			}

		} catch (Exception e) {
			// that's it. errors sending JMS messages should no longer prevent a
			// web request from completing.
			String notificationType = "null";
			if (notificationTemplate.getNotificationType() != null) {
				notificationType = notificationTemplate.getNotificationType().getCode();
			}
			logger.error("Send Delayed Message - JMS Error NotificationTemplate.getType() " + notificationType, e);
			sendFailMeter.mark();
		}
	}

	@Override
	public void sendEventMessage(final Event event) {
		Assert.notNull(event);
		webRequestContextProvider.inject(event);
		try {
			MessageCreator messageCreator = new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					Message message = session.createObjectMessage(event);

					if (event instanceof ScheduledEvent) {
						Calendar scheduleDate = ((ScheduledEvent) event).getScheduledDate();
						if (scheduleDate != null) {
							long timeDelta = DateUtilities.getDifferenceInMillisFromNow(scheduleDate);
							if (timeDelta > 0L) {
								logger.info(String.format("Scheduling Event Message AMQ job [%s] for: %s",
										event.getClass().getName(),
										DateUtilities.getISO8601(scheduleDate)));
								message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, timeDelta);
							}
						}

					}

					if (event.getMessageGroupId() != null) {
						message.setStringProperty("JMSXGroupID", event.getMessageGroupId());
					}

					return message;
				}
			};

			if (event instanceof IndexerEvent) {
				sendSync(reindexEventMessageTemplate, messageCreator);
			} else {
				sendSync(eventMessageTemplate, messageCreator);
			}
		} catch (Exception e) {
			logger.error("Send Event Message - Event " + event.getClass(), e);
			sendFailMeter.mark();
		}
	}

	@Override
	public void sendBatchMessage(final BatchMessageType type) {
		Assert.notNull(type);
		try {
			sendSync(batchTemplate,new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(type);
				}
			});
		}
		catch (InterruptedException e) {
			logger.error("Send Batch Message Failed: " + type.getClass(), e);
			sendFailMeter.mark();
		}
	}

	private void sendSync(final JmsTemplate jmsTemplate, final MessageCreator messageCreator) throws InterruptedException {
		sendAttemptMeter.mark();
		long startTime = System.currentTimeMillis();
		String uuid = UUID.randomUUID().toString();
		logger.info("JMS: Sending Message[" + uuid + "] to " + jmsTemplate.getDefaultDestination().toString());
		jmsTemplate.send(messageCreator);
		long totalMillis = System.currentTimeMillis() - startTime;
		logger.info("JMS: Sent Message[" + uuid + "] to " + jmsTemplate.getDefaultDestination().toString() + " in " + totalMillis + "ms");
		sendSyncMeter.mark();
	}

	private void sendAsync(final JmsTemplate jmsTemplate, final MessageCreator messageCreator) throws InterruptedException {
		new JmsTemplateHystrixCommand(jmsTemplate, messageCreator).queue();
	}

	private class JmsTemplateHystrixCommand extends HystrixCommand<Boolean> {
		private final JmsTemplate jmsTemplate;
		private final MessageCreator messageCreator;
		private final String uuid;
		private final long startTime = System.currentTimeMillis();
		private int retries = 0;

		public JmsTemplateHystrixCommand(JmsTemplate jmsTemplate, MessageCreator messageCreator) {
			super(getHystrixConfig(600000, jmsTemplate.getDefaultDestination().toString()));

			this.jmsTemplate = jmsTemplate;
			this.messageCreator = messageCreator;
			uuid = UUID.randomUUID().toString();
		}

		@Override
		protected Boolean run() throws Exception {
			sendAttemptMeter.mark();
			logger.info("JMS: Sending Message[" + uuid + "] to " + jmsTemplate.getDefaultDestination().toString());
			jmsTemplate.send(messageCreator);
			long totalMillis = System.currentTimeMillis() - startTime;
			logger.info("JMS: Sent Message[" + uuid + "] to " + jmsTemplate.getDefaultDestination().toString() + " in " + totalMillis + "ms");
			sendAsyncMeter.mark();
			return true;
		}

		@Override
		protected Boolean getFallback() {
			long totalMillis = System.currentTimeMillis() - startTime;
			logger.error("Unable to send JMS message[" + uuid + "] to " + jmsTemplate.getDefaultDestination().toString() + " after " + totalMillis + "ms");
			sendFailMeter.mark();
			return false;
		}
	}

	private HystrixCommand.Setter getHystrixConfig(final int timeoutInMillis, String jmsDestination) {
		return HystrixCommand.Setter
			.withGroupKey(HystrixCommandGroupKey.Factory.asKey(COMMAND_GROUP))
			.andCommandKey(
				com.netflix.hystrix.HystrixCommandKey.Factory.asKey(COMMAND_KEY))
			.andCommandPropertiesDefaults(
				HystrixCommandProperties.Setter()
					.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
					.withCircuitBreakerEnabled(false)
					.withExecutionTimeoutInMilliseconds(timeoutInMillis))
			.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("JMS:" + jmsDestination))
			.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
				.withCoreSize(20)
				.withMaxQueueSize(100000)
				.withQueueSizeRejectionThreshold(99999));
	}
}
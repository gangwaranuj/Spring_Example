package com.workmarket.service.business.queue.integration;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.logging.NRTrace;
import com.workmarket.service.business.integration.event.IntegrationEvent;
import com.workmarket.service.business.integration.event.IntegrationListenerService;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;

/**
 * Created by nick on 2012-12-28 10:23 PM
 * Listener/dispatcher for async integration update events
 */
@Service
public class IntegrationListener implements MessageListener {

	private static final Log logger = LogFactory.getLog(IntegrationListener.class);
	@Autowired IntegrationListenerService integrationListenerService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired WebRequestContextProvider webRequestContextProvider;

	private WMMetricRegistryFacade metricFacade;
	private Meter consumeMeter;
	private Histogram consumeDelayHistogram;

	@PostConstruct
	void init() {
		metricFacade = new WMMetricRegistryFacade(metricRegistry, "jms.listener.integration");
		consumeMeter = metricFacade.meter("consume");
		consumeDelayHistogram = metricFacade.histogram("consumeDelay");
	}

	@Override
	@NRTrace(dispatcher=true)
	public void onMessage(Message message) {
		consumeMeter.mark();

		if (!(message instanceof ActiveMQObjectMessage)) {
			logger.error("[integration] invalid message type: " + message);
			return;
		}

		ActiveMQObjectMessage amqMessage = (ActiveMQObjectMessage) message;
		IntegrationEvent data;
		try {
			data = checkNotNull((IntegrationEvent) amqMessage.getObject());
			webRequestContextProvider.extract(data);
		} catch (JMSException e) {
			logger.error("[integration] JMS exception while trying to deserialize an IntegrationEvent. ", e);
			return;
		} catch (Exception e) {
			logger.error("[integration] exception while trying to deserialize an IntegrationEvent. ", e);
			return;
		}

		long delay = 0;

		try { // defensively try/catch in case we run into any serialization issues on deploy
			if (data.getCurrentTimeInMillis() > 0) {
				delay = System.currentTimeMillis() - data.getCurrentTimeInMillis();
			}
		} catch (final Exception e) {
			logger.error("Error marking webhook delay", e);
		}

		String eventType = checkNotNull(data.getEventType()).getCode();
		metricFacade.meter("consume.event." + eventType).mark();
		consumeDelayHistogram.update(delay);
		metricFacade.histogram("consumeDelay.event." + eventType).update(delay);

		try {
			switch (eventType) {
				case IntegrationEventType.WORK_CREATE:
					if (data.getEventArgs().containsKey(IntegrationEvent.WEBHOOK_ID)) {
						integrationListenerService.onWorkCreated(data.getWorkId(), (Long) data.getEventArgs().get(IntegrationEvent.WEBHOOK_ID));
					} else {
						integrationListenerService.onWorkCreated(data.getWorkId(), (Long) data.getEventArgs().get(IntegrationEvent.BUYER_ID), (Long) data.getEventArgs().get(IntegrationEvent.AUTOTASK_ID));
					}
					break;
				case IntegrationEventType.WORK_SEND:
					integrationListenerService.onWorkSent(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_ACCEPT:
					integrationListenerService.onWorkAccepted(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_COMPLETE:
					integrationListenerService.onWorkComplete(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_APPROVE:
					integrationListenerService.onWorkApproved(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_PAY:
					integrationListenerService.onWorkPaid(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_VOID:
					integrationListenerService.onWorkVoided(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_CANCEL:
					integrationListenerService.onWorkCancelled(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_CONFIRM:
					integrationListenerService.onWorkConfirmed(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_CHECK_IN:
					integrationListenerService.onCheckIn(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_CHECK_OUT:
					integrationListenerService.onCheckOut(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_CUSTOM_FIELDS_UPDATE:
					integrationListenerService.onWorkCustomFieldsUpdated(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_ASSET_ADD:
					integrationListenerService.onAttachmentAdded(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_ASSET_REMOVE:
					integrationListenerService.onAttachmentRemoved(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_NOTE_ADD:
					integrationListenerService.onNoteAdded(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_LABEL_ADD:
					integrationListenerService.onLabelAdded(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_LABEL_REMOVE:
					integrationListenerService.onLabelRemoved(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_RESCHEDULE_REQUEST:
					integrationListenerService.onWorkRescheduleRequested(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_RESCHEDULE_APPROVE:
					integrationListenerService.onWorkRescheduleApproved(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_RESCHEDULE_DECLINE:
					integrationListenerService.onWorkRescheduleDeclined(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_BUDGET_INCREASE_ADD:
					integrationListenerService.onBudgetIncreaseAdded(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST:
					integrationListenerService.onBudgetIncreaseRequested(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_BUDGET_INCREASE_APPROVE:
					integrationListenerService.onBudgetIncreaseApproved(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_BUDGET_INCREASE_DECLINE:
					integrationListenerService.onBudgetIncreaseDeclined(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_ADD:
					integrationListenerService.onExpenseReimbursementAdded(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_REQUEST:
					integrationListenerService.onExpenseReimbursementRequested(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_APPROVE:
					integrationListenerService.onExpenseReimbursementApproved(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_DECLINE:
					integrationListenerService.onExpenseReimbursementDeclined(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_BONUS_ADD:
					integrationListenerService.onBonusAdded(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_BONUS_REQUEST:
					integrationListenerService.onBonusRequested(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_BONUS_APPROVE:
					integrationListenerService.onBonusApproved(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_BONUS_DECLINE:
					integrationListenerService.onBonusDeclined(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_NEGOTIATION_REQUEST:
					integrationListenerService.onWorkNegotiationRequested(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_NEGOTIATION_APPROVE:
					integrationListenerService.onWorkNegotiationApproved(data.getWorkId(), data.getEventArgs());
					break;
				case IntegrationEventType.WORK_NEGOTIATION_DECLINE:
					integrationListenerService.onWorkNegotiationDeclined(data.getWorkId(), data.getEventArgs());
					break;
				default:
					logger.error(String.format("[integration] Event type %s not found", eventType));
					break;
			}
		} finally {
			webRequestContextProvider.clear();
		}
	}
}

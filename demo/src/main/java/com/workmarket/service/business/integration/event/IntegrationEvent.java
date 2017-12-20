package com.workmarket.service.business.integration.event;

import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.service.business.queue.WorkMarketEventBean;

import com.workmarket.service.web.AbstractWebRequestContextAware;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static com.workmarket.utility.CollectionUtilities.newObjectMap;

public class IntegrationEvent extends AbstractWebRequestContextAware implements WorkMarketEventBean, Delayed {
	private static final long serialVersionUID = 1677184692236163410L;

	public static final String BUYER_ID = "buyerId";
	public static final String WEBHOOK_ID = "webHookId";
	public static final String AMOUNT = "amount";
	public static final String WORK_SUBSTATUS_TYPE_ASSOCIATION_ID = "workSubStatusTypeAssociationId";
	public static final String IS_AUTOTASK = "autotask";
	public static final String RESOURCE_ID = "resourceId";
	public static final String TIME_TRACKING_ID = "timeTrackingId";
	public static final String NOTE_ID = "noteId";
	public static final String ASSET_ID = "assetId";
	public static final String NEGOTIATION_ID = "negotiationId";
	public static final String AUTOTASK_ID = "autotaskId";
	public static final String NOTIFY_MBO = "notifyMbo";
	public static final String IS_API_TRIGGERED = "apiTriggered";
	private static final long delayTimeMillis = 5000;
	private final IntegrationEventType eventType;
	private final Long workId;
	private final Map<String, Object> eventArgs;
	private final long delayTime;
	private long currentTimeInMillis;

	public IntegrationEvent(IntegrationEventType eventType, Long workId, Map<String, Object> eventArgs) {
		Assert.notNull(eventType);
		this.eventType = eventType;
		this.workId = workId;
		this.eventArgs = eventArgs;
		this.delayTime = delayTimeMillis + System.currentTimeMillis();
		this.currentTimeInMillis = System.currentTimeMillis();
	}

	public static IntegrationEvent newWorkCreateEventAutotask(Long workId, Long buyerId, Long autotaskId) {
		Assert.notNull(buyerId);
		Assert.notNull(autotaskId);

		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_CREATE), workId, newObjectMap(
				BUYER_ID, buyerId,
				AUTOTASK_ID, autotaskId
		));
	}

	public static IntegrationEvent newWorkCreateEvent(Long workId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_CREATE), workId, newObjectMap(
				WEBHOOK_ID, webHookId
		));
	}

	public static IntegrationEvent newWorkAcceptEvent(Long workId, Long resourceId, Long webHookId, boolean isAutotask, boolean notifyMbo) {
		Map<String, Object> eventArgs = newObjectMap(
				IS_AUTOTASK, isAutotask,
				RESOURCE_ID, resourceId,
				NOTIFY_MBO, notifyMbo
		);
		if (!isAutotask) {
			eventArgs.put(WEBHOOK_ID, webHookId);
		}
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_ACCEPT), workId, eventArgs);
	}

	public static IntegrationEvent newWorkNoteAddEvent(Long workId, Long noteId, Long webHookId, boolean isAutotask) {
		Map<String, Object> eventArgs = newObjectMap(
				IS_AUTOTASK, isAutotask,
				NOTE_ID, noteId
		);
		if (!isAutotask) {
			eventArgs.put(WEBHOOK_ID, webHookId);
		}
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_NOTE_ADD), workId, eventArgs);
	}

	public static IntegrationEvent newWorkAssetAddEvent(Long workId, Long assetId, Long webHookId, boolean isAutotask) {
		Map<String, Object> eventArgs = newObjectMap(
				IS_AUTOTASK, isAutotask,
				ASSET_ID, assetId
		);
		if (!isAutotask) {
			eventArgs.put(WEBHOOK_ID, webHookId);
		}
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_ASSET_ADD), workId, eventArgs);
	}

	public static IntegrationEvent newResourceCheckInEvent(Long workId, Long webHookId, Long timeTrackingId, boolean isAutotask) {
		Map<String, Object> eventArgs = newObjectMap(
				IS_AUTOTASK, isAutotask,
				TIME_TRACKING_ID, timeTrackingId
		);
		if (!isAutotask) {
			eventArgs.put(WEBHOOK_ID, webHookId);
		}
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_CHECK_IN), workId, eventArgs);
	}

	public static IntegrationEvent newResourceCheckOutEvent(Long workId, Long webHookId, Long timeTrackingId, boolean isAutotask) {
		Map<String, Object> eventArgs = newObjectMap(
				IS_AUTOTASK, isAutotask,
				TIME_TRACKING_ID, timeTrackingId
		);
		if (!isAutotask) {
			eventArgs.put(WEBHOOK_ID, webHookId);
		}
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_CHECK_OUT), workId, eventArgs);
	}

	public static IntegrationEvent newWorkCustomFieldsUpdatedEvent(Long workId, Long webHookId, boolean isAutotask, boolean isApiTriggered) {
		Map<String, Object> eventArgs = newObjectMap(
				IS_API_TRIGGERED, isApiTriggered,
				IS_AUTOTASK, isAutotask
		);
		if (!isAutotask) {
			eventArgs.put(WEBHOOK_ID, webHookId);
		}
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_CUSTOM_FIELDS_UPDATE), workId, eventArgs);
	}

	public static IntegrationEvent newWorkCompleteEvent(Long workId, Long webHookId, boolean isAutotask) {
		Map<String, Object> eventArgs = newObjectMap(
				IS_AUTOTASK, isAutotask
		);
		if (!isAutotask) {
			eventArgs.put(WEBHOOK_ID, webHookId);
		}
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_COMPLETE), workId, eventArgs);
	}

	public static IntegrationEvent newWorkApproveEvent(Long workId, Long webHookId, boolean isAutotask) {
		Map<String, Object> eventArgs = newObjectMap(
				IS_AUTOTASK, isAutotask
		);
		if (!isAutotask) {
			eventArgs.put(WEBHOOK_ID, webHookId);
		}
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_APPROVE), workId, eventArgs);
	}

	public static IntegrationEvent newWorkSentEvent(Long workId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_SEND), workId, newObjectMap(
				WEBHOOK_ID, webHookId
		));
	}

	public static IntegrationEvent newWorkPaidEvent(Long workId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_PAY), workId, newObjectMap(
				WEBHOOK_ID, webHookId
		));
	}

	public static IntegrationEvent newWorkVoidedEvent(Long workId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_VOID), workId, newObjectMap(
				WEBHOOK_ID, webHookId
		));
	}

	public static IntegrationEvent newWorkCancelEvent(Long workId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_CANCEL), workId, newObjectMap(
				WEBHOOK_ID, webHookId
		));
	}

	public static IntegrationEvent newWorkConfirmedEvent(Long workId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_CONFIRM), workId, newObjectMap(
				WEBHOOK_ID, webHookId
		));
	}

	public static IntegrationEvent newWorkAssetRemoveEvent(Long workId, Long webHookId, Long assetId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_ASSET_REMOVE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				ASSET_ID, assetId
		));
	}

	public static IntegrationEvent newWorkLabelAddEvent(Long workId, Long webHookId, Long workSubStatusTypeAssociationId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_LABEL_ADD), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				WORK_SUBSTATUS_TYPE_ASSOCIATION_ID, workSubStatusTypeAssociationId
		));
	}

	public static IntegrationEvent newWorkLabelRemoveEvent(Long workId, Long webHookId, Long workSubStatusTypeAssociationId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_LABEL_REMOVE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				WORK_SUBSTATUS_TYPE_ASSOCIATION_ID, workSubStatusTypeAssociationId
		));
	}

	public static IntegrationEvent newWorkNegotiationRequestEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_NEGOTIATION_REQUEST), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkNegotiationAddEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_NEGOTIATION_ADD), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkNegotiationApproveEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_NEGOTIATION_APPROVE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkNegotiationDeclineEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_NEGOTIATION_DECLINE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkRescheduleRequestEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_RESCHEDULE_REQUEST), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkRescheduleApproveEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_RESCHEDULE_APPROVE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkRescheduleDeclineEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_RESCHEDULE_DECLINE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkBudgetIncreaseAddEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_BUDGET_INCREASE_ADD), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkBudgetIncreaseRequestEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkBudgetIncreaseApproveEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_BUDGET_INCREASE_APPROVE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkBudgetIncreaseDeclineEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_BUDGET_INCREASE_DECLINE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkExpenseReimbursementAddEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_ADD), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkExpenseReimbursementRequestEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_REQUEST), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkExpenseReimbursementApproveEvent(Long workId, Long negotiationId, Long webHookId, BigDecimal amount) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_APPROVE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId,
				AMOUNT, amount
		));
	}

	public static IntegrationEvent newWorkExpenseReimbursementDeclineEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_DECLINE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkBonusAddEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_BONUS_ADD), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkBonusRequestEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_BONUS_REQUEST), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public static IntegrationEvent newWorkBonusApproveEvent(Long workId, Long negotiationId, Long webHookId, BigDecimal amount) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_BONUS_APPROVE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId,
				AMOUNT, amount
		));
	}

	public static IntegrationEvent newWorkBonusDeclineEvent(Long workId, Long negotiationId, Long webHookId) {
		return new IntegrationEvent(IntegrationEventType.newInstance(IntegrationEventType.WORK_BONUS_DECLINE), workId, newObjectMap(
				WEBHOOK_ID, webHookId,
				NEGOTIATION_ID, negotiationId
		));
	}

	public IntegrationEventType getEventType() {
		return eventType;
	}

	public Long getWorkId() {
		return workId;
	}

	public Map<String, Object> getEventArgs() {
		return eventArgs;
	}

	public long getCurrentTimeInMillis() {
		return currentTimeInMillis;
	}

	public void setCurrentTimeInMillis(final long currentTimeInMillis) {
		this.currentTimeInMillis = currentTimeInMillis;
	}

	@Override
	public long getDelay(TimeUnit timeUnit) {
		return timeUnit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Delayed delayed) {
		IntegrationEvent delay = (IntegrationEvent) delayed;
		return (int) (this.workId - delay.getWorkId());
	}

	@Override
	public Long getId() {
		return getWorkId();
	}
}

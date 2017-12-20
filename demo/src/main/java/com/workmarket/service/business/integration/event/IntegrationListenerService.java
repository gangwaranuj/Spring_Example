package com.workmarket.service.business.integration.event;

import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;

import java.util.Map;

/**
 * Created by nick on 2012-12-24 2:30 PM
 */
public interface IntegrationListenerService {
	public boolean doAuthentication(WebHook webHook, Map <String, String> authenticationHeaders, boolean skipCache);
	public boolean doRequest(WebHook webHook, ParsedWebHookDTO parsedWebHookDTO);
	public boolean runWebHook(AbstractWork work, WebHook webHook, Map<String, String> contextVariables);

	public boolean onWorkCreated(Long workId, Long buyerId, Long autotaskId);
	public boolean onWorkCreated(Long workId, Long webHookId);

	public boolean onWorkSent(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkAccepted(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkComplete(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkApproved(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkPaid(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkVoided(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkCancelled(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkConfirmed(Long workId, Map<String, Object> eventArguments);

	public boolean onCheckIn(Long workId, Map<String, Object> eventArguments);
	public boolean onCheckOut(Long workId, Map<String, Object> eventArguments);

	public boolean onWorkCustomFieldsUpdated(Long workId, Map<String, Object> eventArguments);

	public boolean onAttachmentAdded(Long workId, Map<String, Object> eventArguments);
	public boolean onAttachmentRemoved(Long workId, Map<String, Object> eventArguments);

	public boolean onNoteAdded(Long workId, Map<String, Object> eventArguments);

	public boolean onLabelAdded(Long workId, Map<String, Object> eventArguments);
	public boolean onLabelRemoved(Long workId, Map<String, Object> eventArguments);

	public boolean onWorkNegotiationRequested(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkNegotiationApproved(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkNegotiationDeclined(Long workId, Map<String, Object> eventArguments);

	public boolean onWorkRescheduleRequested(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkRescheduleApproved(Long workId, Map<String, Object> eventArguments);
	public boolean onWorkRescheduleDeclined(Long workId, Map<String, Object> eventArguments);

	public boolean onBudgetIncreaseAdded(Long workId, Map<String, Object> eventArguments);
	public boolean onBudgetIncreaseRequested(Long workId, Map<String, Object> eventArguments);
	public boolean onBudgetIncreaseApproved(Long workId, Map<String, Object> eventArguments);
	public boolean onBudgetIncreaseDeclined(Long workId, Map<String, Object> eventArguments);

	public boolean onExpenseReimbursementAdded(Long workId, Map<String, Object> eventArguments);
	public boolean onExpenseReimbursementRequested(Long workId, Map<String, Object> eventArguments);
	public boolean onExpenseReimbursementApproved(Long workId, Map<String, Object> eventArguments);
	public boolean onExpenseReimbursementDeclined(Long workId, Map<String, Object> eventArguments);

	public boolean onBonusAdded(Long workId, Map<String, Object> eventArguments);
	public boolean onBonusRequested(Long workId, Map<String, Object> eventArguments);
	public boolean onBonusApproved(Long workId, Map<String, Object> eventArguments);
	public boolean onBonusDeclined(Long workId, Map<String, Object> eventArguments);
}

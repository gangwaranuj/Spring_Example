package com.workmarket.domains.model.integration;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity(name = "integrationEventType")
@Table(name = "integration_event_type")
public class IntegrationEventType extends LookupEntity {

	private static final long serialVersionUID = -1146659609498819971L;

	private String title;

	public static final String WORK_CREATE = "workCreate";
	public static final String WORK_SEND = "workSend";
	public static final String WORK_ACCEPT = "workAccept";
	public static final String WORK_COMPLETE = "workComplete";
	public static final String WORK_APPROVE = "workApprove";
	public static final String WORK_PAY = "workPay";
	public static final String WORK_VOID = "workVoid";
	public static final String WORK_CANCEL = "workCancel";
	public static final String WORK_CONFIRM = "workConfirm";
	public static final String WORK_CHECK_IN = "workCheckIn";
	public static final String WORK_CHECK_OUT = "workCheckOut";
	public static final String WORK_CUSTOM_FIELDS_UPDATE = "workCustomFieldsUpdate";
	public static final String WORK_ASSET_ADD = "workAttachmentAdd";
	public static final String WORK_ASSET_REMOVE = "workAttachmentRemove";
	public static final String WORK_NOTE_ADD = "workNoteAdd";
	public static final String WORK_LABEL_ADD = "workLabelAdd";
	public static final String WORK_LABEL_REMOVE = "workLabelRemove";
	public static final String WORK_NEGOTIATION_REQUEST = "workNegotiationRequest";
	public static final String WORK_NEGOTIATION_ADD = "workNegotiationAdd";
	public static final String WORK_NEGOTIATION_APPROVE = "workNegotiationApprove";
	public static final String WORK_NEGOTIATION_DECLINE = "workNegotiationDecline";
	public static final String WORK_RESCHEDULE_REQUEST = "workRescheduleRequest";
	public static final String WORK_RESCHEDULE_APPROVE = "workRescheduleApprove";
	public static final String WORK_RESCHEDULE_DECLINE = "workRescheduleDecline";
	public static final String WORK_BUDGET_INCREASE_ADD = "workBudgetIncreaseAdd";
	public static final String WORK_BUDGET_INCREASE_REQUEST = "workBudgetIncreaseRequest";
	public static final String WORK_BUDGET_INCREASE_APPROVE = "workBudgetIncreaseApprove";
	public static final String WORK_BUDGET_INCREASE_DECLINE = "workBudgetIncreaseDecline";
	public static final String WORK_EXPENSE_REIMBURSEMENT_ADD = "workExpenseReimbursementAdd";
	public static final String WORK_EXPENSE_REIMBURSEMENT_REQUEST = "workExpenseReimbursementRequest";
	public static final String WORK_EXPENSE_REIMBURSEMENT_APPROVE = "workExpenseReimbursementApprove";
	public static final String WORK_EXPENSE_REIMBURSEMENT_DECLINE = "workExpenseReimbursementDecline";
	public static final String WORK_BONUS_ADD = "workBonusAdd";
	public static final String WORK_BONUS_REQUEST = "workBonusRequest";
	public static final String WORK_BONUS_APPROVE = "workBonusApprove";
	public static final String WORK_BONUS_DECLINE = "workBonusDecline";

	public static Set<String> ALL_RESOURCE = new ImmutableSet.Builder<String>()
		.add(WORK_ACCEPT)
		.add(WORK_NEGOTIATION_REQUEST)
		.build();

	public static Set<String> ALL_EVENTS = new ImmutableSet.Builder<String>()
		.add(WORK_CREATE)
		.add(WORK_SEND)
		.add(WORK_ACCEPT)
		.add(WORK_COMPLETE)
		.add(WORK_APPROVE)
		.add(WORK_PAY)
		.add(WORK_VOID)
		.add(WORK_CANCEL)
		.add(WORK_CONFIRM)
		.add(WORK_CHECK_IN)
		.add(WORK_CHECK_OUT)
		.add(WORK_CUSTOM_FIELDS_UPDATE)
		.add(WORK_ASSET_ADD)
		.add(WORK_ASSET_REMOVE)
		.add(WORK_NOTE_ADD)
		.add(WORK_LABEL_ADD)
		.add(WORK_LABEL_REMOVE)
		.add(WORK_NEGOTIATION_REQUEST)
		.add(WORK_NEGOTIATION_ADD)
		.add(WORK_NEGOTIATION_APPROVE)
		.add(WORK_NEGOTIATION_DECLINE)
		.add(WORK_RESCHEDULE_REQUEST)
		.add(WORK_RESCHEDULE_APPROVE)
		.add(WORK_RESCHEDULE_DECLINE)
		.add(WORK_BUDGET_INCREASE_ADD)
		.add(WORK_BUDGET_INCREASE_REQUEST)
		.add(WORK_BUDGET_INCREASE_APPROVE)
		.add(WORK_BUDGET_INCREASE_DECLINE)
		.add(WORK_EXPENSE_REIMBURSEMENT_ADD)
		.add(WORK_EXPENSE_REIMBURSEMENT_REQUEST)
		.add(WORK_EXPENSE_REIMBURSEMENT_APPROVE)
		.add(WORK_EXPENSE_REIMBURSEMENT_DECLINE)
		.add(WORK_BONUS_ADD)
		.add(WORK_BONUS_REQUEST)
		.add(WORK_BONUS_APPROVE)
		.add(WORK_BONUS_DECLINE)
		.build();

	public static IntegrationEventType newInstance(String code) {
		IntegrationEventType result = new IntegrationEventType();
		result.setCode(code);
		return result;
	}

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}

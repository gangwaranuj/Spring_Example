package com.workmarket.integration.webhook;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.integration.IntegrationEventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum WebHookDispatchField {
	// general fields
	ASSIGNMENT_ID("assignment_id", IntegrationEventType.ALL_EVENTS),
	STATUS("status", IntegrationEventType.ALL_EVENTS),
	WORK_START_TIME("start_date_time", IntegrationEventType.ALL_EVENTS),
	WORK_END_TIME("end_date_time", IntegrationEventType.ALL_EVENTS),
	PRICING_TYPE("pricing_type", IntegrationEventType.ALL_EVENTS),
	PRICING_FLAT_PRICE("pricing_flat_price", IntegrationEventType.ALL_EVENTS),
	PRICING_PER_HOUR_PRICE("pricing_per_hour_price", IntegrationEventType.ALL_EVENTS),
	PRICING_MAX_NUMBER_OF_HOURS("pricing_max_number_of_hours", IntegrationEventType.ALL_EVENTS),
	PRICING_PER_UNIT_PRICE("pricing_per_unit_price", IntegrationEventType.ALL_EVENTS),
	PRICING_MAX_NUMBER_OF_UNITS("pricing_max_number_of_units", IntegrationEventType.ALL_EVENTS),
	PRICING_ADDITIONAL_PER_HOUR_PRICE("pricing_additional_per_hour_price", IntegrationEventType.ALL_EVENTS),
	PRICING_MAX_ADDITIONAL_NUMBER_OF_HOURS("pricing_max_additional_number_of_hours", IntegrationEventType.ALL_EVENTS),
	RESOLUTION("resolution", IntegrationEventType.ALL_EVENTS),
	OVERRIDE_PRICE("override_price", IntegrationEventType.ALL_EVENTS),
	HOURS_WORKED("hours_worked", IntegrationEventType.ALL_EVENTS),
	UNITS_COMPLETED("units_completed", IntegrationEventType.ALL_EVENTS),
	EXPENSE_REIMBURSEMENT("expense_reimbursement", IntegrationEventType.ALL_EVENTS),
	BONUS("bonus", IntegrationEventType.ALL_EVENTS),
	TOTAL_COST("total_cost", IntegrationEventType.ALL_EVENTS),
	INVOICE_ID("invoice_id", IntegrationEventType.ALL_EVENTS),
	STATEMENT_ID("statement_id", IntegrationEventType.ALL_EVENTS),
	OWNER_ID("owner_id", IntegrationEventType.ALL_EVENTS),
	OWNER_EMAIL("owner_email", IntegrationEventType.ALL_EVENTS),
	CLIENT_NAME("client_name", IntegrationEventType.ALL_EVENTS),
	CLIENT_ID("client_id", IntegrationEventType.ALL_EVENTS),
	CLIENT_CUSTOMER_ID("client_customer_id", IntegrationEventType.ALL_EVENTS),
	PROJECT_ID("project_id", IntegrationEventType.ALL_EVENTS),
	PROJECT_NAME("project_name", IntegrationEventType.ALL_EVENTS),
	SUPPORT_CONTACT_FIRST_NAME("support_contact_first_name", IntegrationEventType.ALL_EVENTS),
	SUPPORT_CONTACT_LAST_NAME("support_contact_last_name", IntegrationEventType.ALL_EVENTS),
	SUPPORT_CONTACT_EMAIL("support_contact_email", IntegrationEventType.ALL_EVENTS),
	NOW("now", IntegrationEventType.ALL_EVENTS),

	// active resource fields
	RESOURCE_ID("resource_id", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_UUID("resource_uuid", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_FIRST_NAME("resource_first_name", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_LAST_NAME("resource_last_name", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_PHONE("resource_phone", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_MOBILE("resource_mobile", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_ADDRESS1("resource_address1", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_ADDRESS2("resource_address2", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_CITY("resource_city", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_STATE("resource_state", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_POSTAL_CODE("resource_postal_code", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_COUNTRY("resource_country", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_EMAIL("resource_email", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_OVERVIEW("resource_overview", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_COMPANY_NAME("resource_company_name", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_COMPANY_ID("resource_company_id", IntegrationEventType.ALL_RESOURCE),
	RESOURCE_COMPANY_UUID("resource_company_uuid", IntegrationEventType.ALL_RESOURCE),

	// specific for events
	NOTE("note", Sets.newHashSet(
			IntegrationEventType.WORK_CHECK_OUT,
			IntegrationEventType.WORK_LABEL_REMOVE,
			IntegrationEventType.WORK_LABEL_ADD,
			IntegrationEventType.WORK_NOTE_ADD,
			IntegrationEventType.WORK_NEGOTIATION_REQUEST,
			IntegrationEventType.WORK_NEGOTIATION_DECLINE,
			IntegrationEventType.WORK_BONUS_ADD,
			IntegrationEventType.WORK_BONUS_REQUEST,
			IntegrationEventType.WORK_BONUS_DECLINE,
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_ADD,
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_REQUEST,
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_DECLINE,
			IntegrationEventType.WORK_BUDGET_INCREASE_ADD,
			IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST,
			IntegrationEventType.WORK_BUDGET_INCREASE_DECLINE,
			IntegrationEventType.WORK_RESCHEDULE_REQUEST,
			IntegrationEventType.WORK_RESCHEDULE_DECLINE
			)),
	CHECKED_IN_ON("checked_in_on", Sets.newHashSet(
			IntegrationEventType.WORK_CHECK_IN,
			IntegrationEventType.WORK_CHECK_OUT)),
	CHECKED_OUT_ON("checked_out_on", Sets.newHashSet(
			IntegrationEventType.WORK_CHECK_OUT)),
	CHECK_IN_OUT_ID("check_in_out_id", Sets.newHashSet(
			IntegrationEventType.WORK_CHECK_IN,
			IntegrationEventType.WORK_CHECK_OUT)),
	LABEL_NAME("label_name", Sets.newHashSet(
			IntegrationEventType.WORK_LABEL_ADD,
			IntegrationEventType.WORK_LABEL_REMOVE)),
	LABEL_ID("label_id", Sets.newHashSet(
			IntegrationEventType.WORK_LABEL_ADD,
			IntegrationEventType.WORK_LABEL_REMOVE)),
	IS_NEGOTIATION("is_negotiation", Sets.newHashSet(
			IntegrationEventType.WORK_LABEL_ADD,
			IntegrationEventType.WORK_LABEL_REMOVE)),
	AMOUNT("amount", Sets.newHashSet(
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_REQUEST,
			IntegrationEventType.WORK_BONUS_REQUEST,
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_APPROVE,
			IntegrationEventType.WORK_BONUS_APPROVE)),
	PROPOSED_FLAT_PRICE("proposed_flat_price", Sets.newHashSet(
			IntegrationEventType.WORK_NEGOTIATION_REQUEST,
			IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST)),
	PROPOSED_MAX_INITIAL_HOURS("proposed_max_initial_number_of_hours", Sets.newHashSet(
			IntegrationEventType.WORK_NEGOTIATION_REQUEST,
			IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST)),
	PROPOSED_MAX_ADDITIONAL_HOURS("proposed_max_additional_number_of_hours", Sets.newHashSet(
			IntegrationEventType.WORK_NEGOTIATION_REQUEST,
			IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST)),
	PROPOSED_MAX_UNITS("proposed_max_number_of_units", Sets.newHashSet(
			IntegrationEventType.WORK_NEGOTIATION_REQUEST,
			IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST)),
	PROPOSED_START_DATE_TIME("proposed_start_date_time",  Sets.newHashSet(
			IntegrationEventType.WORK_NEGOTIATION_REQUEST,
			IntegrationEventType.WORK_RESCHEDULE_REQUEST)),
	PROPOSED_END_DATE_TIME("proposed_end_date_time",  Sets.newHashSet(
			IntegrationEventType.WORK_NEGOTIATION_REQUEST,
			IntegrationEventType.WORK_RESCHEDULE_REQUEST)),
	PROPOSED_EXPENSE_REIMBURSEMENT_AMOUNT("proposed_expense_reimbursement_amount", Sets.newHashSet(
			IntegrationEventType.WORK_NEGOTIATION_REQUEST)),
	FILE_NAME("file_name", Sets.newHashSet(
			IntegrationEventType.WORK_ASSET_ADD,
			IntegrationEventType.WORK_ASSET_REMOVE)),
	FILE_DESCRIPTION("file_description", Sets.newHashSet(
			IntegrationEventType.WORK_ASSET_ADD,
			IntegrationEventType.WORK_ASSET_REMOVE)),
	FILE_UUID("file_uuid", Sets.newHashSet(
			IntegrationEventType.WORK_ASSET_ADD,
			IntegrationEventType.WORK_ASSET_REMOVE)),
	FILE_DATA("file_data", Sets.newHashSet(
			IntegrationEventType.WORK_ASSET_ADD)),
	FILE_DATA_BASE_64("file_data_base64", Sets.newHashSet(
			IntegrationEventType.WORK_ASSET_ADD)),
	FILE_DATA_RAW("file_data_binary", Sets.newHashSet(
			IntegrationEventType.WORK_ASSET_ADD)),
	FILE_DATA_BYTE_LENGTH("file_data_byte_length", Sets.newHashSet(
			IntegrationEventType.WORK_ASSET_ADD)),
	NEGOTIATION_ID("negotiation_id", Sets.newHashSet(
			IntegrationEventType.WORK_NEGOTIATION_ADD,
			IntegrationEventType.WORK_NEGOTIATION_REQUEST,
			IntegrationEventType.WORK_NEGOTIATION_APPROVE,
			IntegrationEventType.WORK_NEGOTIATION_DECLINE,
			IntegrationEventType.WORK_BONUS_ADD,
			IntegrationEventType.WORK_BONUS_REQUEST,
			IntegrationEventType.WORK_BONUS_APPROVE,
			IntegrationEventType.WORK_BONUS_DECLINE,
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_ADD,
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_REQUEST,
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_APPROVE,
			IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_DECLINE,
			IntegrationEventType.WORK_BUDGET_INCREASE_ADD,
			IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST,
			IntegrationEventType.WORK_BUDGET_INCREASE_APPROVE,
			IntegrationEventType.WORK_BUDGET_INCREASE_DECLINE,
			IntegrationEventType.WORK_RESCHEDULE_REQUEST,
			IntegrationEventType.WORK_RESCHEDULE_APPROVE,
			IntegrationEventType.WORK_RESCHEDULE_DECLINE
	));

	public static final List<String> WEB_HOOK_GENERAL_FIELDS;
	public static final Map<String, List<String>> WEB_HOOK_EVENT_FIELDS;

	static {
		List<String> generalFields = new ArrayList<>();
		Map<String, List<String>> eventFields = new HashMap<>();

		for (WebHookDispatchField field : WebHookDispatchField.values()) {
			if (IntegrationEventType.ALL_EVENTS.size() == field.getEligibleEvents().size()) {
				generalFields.add(field.getFieldName());
			} else {
				for (String event : field.getEligibleEvents()) {
					if (!eventFields.containsKey(event))
						eventFields.put(event, new ArrayList<String>());

					eventFields.get(event).add(field.getFieldName());
				}
			}
		}

		WEB_HOOK_GENERAL_FIELDS = generalFields;
		WEB_HOOK_EVENT_FIELDS = eventFields;
	}

	private final String fieldName;
	private final Set<String> eligibleEvents;

	private WebHookDispatchField(String fieldName, Set<String> eligibleEvents){
		this.fieldName = fieldName;
		this.eligibleEvents = eligibleEvents;
	}

	public static WebHookDispatchField findByFieldName(String fieldName) {
		for(WebHookDispatchField field : WebHookDispatchField.values()) {
			if(field.getFieldName().equals(fieldName)) {
				return field;
			}
		}
		return null;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Set<String> getEligibleEvents() {
		return eligibleEvents;
	}
}

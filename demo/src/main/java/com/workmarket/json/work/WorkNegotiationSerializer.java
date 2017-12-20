package com.workmarket.json.work;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;

import java.lang.reflect.Type;
import java.util.Calendar;

/**
 * Author: rocio
 */
public class WorkNegotiationSerializer implements JsonSerializer<WorkNegotiation> {

	private static final WorkNegotiationSerializer INSTANCE = new WorkNegotiationSerializer();

	private WorkNegotiationSerializer() {}

	public static WorkNegotiationSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(WorkNegotiation workNegotiation, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		if (workNegotiation.getId() != null) {
			jsonObject.addProperty("id", workNegotiation.getId());
		}
		if (workNegotiation.getApprovalStatus() != null) {
			jsonObject.addProperty("approvalStatus", workNegotiation.getApprovalStatus().toString());
		}
		if (workNegotiation.getExpiresOn() != null) {
			jsonObject.add("expiresOn", jsonSerializationContext.serialize(workNegotiation.getExpiresOn(), Calendar.class));
		}
		if (workNegotiation.isPriceNegotiation() != null) {
			jsonObject.addProperty("priceNegotiation", workNegotiation.isPriceNegotiation());
		}
		if (workNegotiation.isScheduleNegotiation() != null) {
			jsonObject.addProperty("scheduleNegotiation", workNegotiation.isScheduleNegotiation());
		}
		if (workNegotiation.getScheduleRangeFlag() != null) {
			jsonObject.addProperty("scheduleRangeFlag", workNegotiation.getScheduleRangeFlag());
		}
		if (workNegotiation.getScheduleFrom() != null) {
			jsonObject.add("scheduleFrom", jsonSerializationContext.serialize(workNegotiation.getScheduleFrom(), Calendar.class));
		}
		if (workNegotiation.getScheduleThrough() != null) {
			jsonObject.add("scheduleThrough", jsonSerializationContext.serialize(workNegotiation.getScheduleThrough(), Calendar.class));
		}
		if (workNegotiation.getRequestedOn() != null) {
			jsonObject.add("requestedOn", jsonSerializationContext.serialize(workNegotiation.getRequestedOn(), Calendar.class));
		}
		if (workNegotiation.getApprovedOn() != null) {
			jsonObject.add("approvedOn", jsonSerializationContext.serialize(workNegotiation.getApprovedOn(), Calendar.class));
		}
		if (workNegotiation.isInitiatedByResource() != null) {
			jsonObject.addProperty("initiatedByResource", workNegotiation.isInitiatedByResource());
		}
		if (workNegotiation.isDuringCompletion() != null) {
			jsonObject.addProperty("duringCompletion", workNegotiation.isDuringCompletion());
		}
		if (workNegotiation.getFullPricingStrategy() != null) {
			jsonObject.add("fullPricingStrategy", jsonSerializationContext.serialize(workNegotiation.getFullPricingStrategy()));
		}
		return jsonObject;
	}
}

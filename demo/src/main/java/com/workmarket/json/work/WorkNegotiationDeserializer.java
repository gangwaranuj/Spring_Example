package com.workmarket.json.work;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;

import java.lang.reflect.Type;
import java.util.Calendar;

/**
 * Author: rocio
 */
public class WorkNegotiationDeserializer implements JsonDeserializer<WorkNegotiation> {

	private static final WorkNegotiationDeserializer INSTANCE = new WorkNegotiationDeserializer();

	private WorkNegotiationDeserializer() {
	}

	public static WorkNegotiationDeserializer getInstance() {
		return INSTANCE;
	}

	@Override public WorkNegotiation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		WorkNegotiation workNegotiation = new WorkNegotiation();

		JsonElement field = jsonObject.get("id");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setId(field.getAsLong());
		}
		field = jsonObject.get("approvalStatus");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setApprovalStatus(ApprovalStatus.valueOf(field.getAsString()));
		}
		field = jsonObject.get("expiresOn");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setExpiresOn((Calendar)jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("priceNegotiation");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setPriceNegotiation(field.getAsBoolean());
		}
		field = jsonObject.get("scheduleNegotiation");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setScheduleNegotiation(field.getAsBoolean());
		}
		field = jsonObject.get("expiresOn");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setExpiresOn((Calendar)jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("scheduleRangeFlag");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setScheduleRangeFlag(field.getAsBoolean());
		}
		field = jsonObject.get("scheduleFrom");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setScheduleFrom((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("scheduleThrough");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setScheduleThrough((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("requestedOn");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setRequestedOn((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("approvedOn");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setApprovedOn((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("initiatedByResource");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setInitiatedByResource(field.getAsBoolean());
		}
		field = jsonObject.get("duringCompletion");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setDuringCompletion(field.getAsBoolean());
		}
		field = jsonObject.get("fullPricingStrategy");
		if (field != null && !field.isJsonNull()) {
			workNegotiation.setFullPricingStrategy((FullPricingStrategy)jsonDeserializationContext.deserialize(field, FullPricingStrategy.class));
		}

		return workNegotiation;
	}
}

package com.workmarket.json.kpi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.data.report.internal.AssignmentReport;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class AssignmentReportDeserializer implements JsonDeserializer<AssignmentReport> {

	private static final AssignmentReportDeserializer INSTANCE = new AssignmentReportDeserializer();

	private AssignmentReportDeserializer() {
	}

	public static AssignmentReportDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public AssignmentReport deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		AssignmentReport assignmentReport = new AssignmentReport();

		JsonElement field = jsonObject.get("voidRate");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setVoidRate(field.getAsDouble());
		}

		field = jsonObject.get("cancelRate");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setCancelRate(field.getAsDouble());
		}

		field = jsonObject.get("paidRate");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setPaidRate(field.getAsDouble());
		}

		field = jsonObject.get("sentRate");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setSentRate(field.getAsDouble());
		}

		field = jsonObject.get("workSend");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setWorkSend(field.getAsDouble());
		}

		field = jsonObject.get("workFeed");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setWorkSend(field.getAsDouble());
		}

		field = jsonObject.get("userSend");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setUserSend(field.getAsDouble());
		}

		field = jsonObject.get("groups");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setGroups(field.getAsDouble());
		}

		field = jsonObject.get("search");
		if (field != null && !field.isJsonNull()) {
			assignmentReport.setSearch(field.getAsDouble());
		}
		return assignmentReport;
	}
}

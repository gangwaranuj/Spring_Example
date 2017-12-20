package com.workmarket.json.kpi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.data.report.internal.AssignmentReport;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class AssignmentReportSerializer implements JsonSerializer<AssignmentReport> {

	private static final AssignmentReportSerializer INSTANCE = new AssignmentReportSerializer();

	private AssignmentReportSerializer() {}

	public static AssignmentReportSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(AssignmentReport assignmentReport, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("voidRate", assignmentReport.getVoidRate());
		jsonObject.addProperty("cancelRate", assignmentReport.getCancelRate());
		jsonObject.addProperty("paidRate", assignmentReport.getPaidRate());
		jsonObject.addProperty("sentRate", assignmentReport.getSentRate());
		jsonObject.addProperty("workSend", assignmentReport.getWorkSend());
		jsonObject.addProperty("workFeed", assignmentReport.getWorkFeed());
		jsonObject.addProperty("userSend", assignmentReport.getUserSend());
		jsonObject.addProperty("groups", assignmentReport.getGroups());
		jsonObject.addProperty("search", assignmentReport.getSearch());
		return jsonObject;
	}
}

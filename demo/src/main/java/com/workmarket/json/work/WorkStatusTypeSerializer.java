package com.workmarket.json.work;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.WorkStatusType;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class WorkStatusTypeSerializer implements JsonSerializer<WorkStatusType> {

	private static final WorkStatusTypeSerializer INSTANCE = new WorkStatusTypeSerializer();

	private WorkStatusTypeSerializer() {}

	public static WorkStatusTypeSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(WorkStatusType workStatusType, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("code", workStatusType.getCode());
		jsonObject.addProperty("description", workStatusType.getDescription());
		return jsonObject;
	}
}

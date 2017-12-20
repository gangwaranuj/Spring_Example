package com.workmarket.json.work;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.WorkStatusType;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class WorkStatusTypeDeserializer implements JsonDeserializer<WorkStatusType> {

	private static final WorkStatusTypeDeserializer INSTANCE = new WorkStatusTypeDeserializer();

	private WorkStatusTypeDeserializer() {
	}

	public static WorkStatusTypeDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public WorkStatusType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		WorkStatusType workStatusType = new WorkStatusType();

		JsonElement field = jsonObject.get("code");
		if (field != null && !field.isJsonNull()) {
			workStatusType.setCode(field.getAsString());
		}

		field = jsonObject.get("description");
		if (field != null && !field.isJsonNull()) {
			workStatusType.setDescription(field.getAsString());
		}
		return workStatusType;
	}
}

package com.workmarket.domains.model.requirementset.backgroundcheck;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BackgroundCheckRequirementSerializer implements JsonSerializer<BackgroundCheckRequirement> {
	private static final BackgroundCheckRequirementSerializer INSTANCE = new BackgroundCheckRequirementSerializer();

	private BackgroundCheckRequirementSerializer(){}
	public static BackgroundCheckRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(BackgroundCheckRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("name", BackgroundCheckRequirement.DEFAULT_NAME);
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		return jsonObject;
	}
}

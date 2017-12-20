package com.workmarket.domains.model.requirementset.rating;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class RatingRequirementSerializer implements JsonSerializer<RatingRequirement> {
	private static final RatingRequirementSerializer INSTANCE = new RatingRequirementSerializer();

	private RatingRequirementSerializer(){}
	public static RatingRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(RatingRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("value", requirement.getValue());
		jsonObject.addProperty("name", requirement.getName());
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		return jsonObject;
	}
}

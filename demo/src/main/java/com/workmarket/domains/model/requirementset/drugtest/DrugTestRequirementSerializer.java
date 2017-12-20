package com.workmarket.domains.model.requirementset.drugtest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class DrugTestRequirementSerializer implements JsonSerializer<DrugTestRequirement> {
	private static final DrugTestRequirementSerializer INSTANCE = new DrugTestRequirementSerializer();

	private DrugTestRequirementSerializer(){}
	public static DrugTestRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(DrugTestRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("name", DrugTestRequirement.DEFAULT_NAME);
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		return jsonObject;
	}
}

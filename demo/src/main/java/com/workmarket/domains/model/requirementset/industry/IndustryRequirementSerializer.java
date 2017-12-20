package com.workmarket.domains.model.requirementset.industry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class IndustryRequirementSerializer implements JsonSerializer<IndustryRequirement> {
	private static final IndustryRequirementSerializer INSTANCE = new IndustryRequirementSerializer();

	private IndustryRequirementSerializer(){}
	public static IndustryRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(IndustryRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		JsonObject industryObject = new JsonObject();
		industryObject.addProperty("name", requirement.getIndustryRequirable().getName());
		industryObject.addProperty("id", requirement.getIndustryRequirable().getId());
		jsonObject.add("requirable", industryObject);

		return jsonObject;
	}
}

package com.workmarket.domains.model.requirementset.esignature;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class EsignatureRequirementSerializer implements JsonSerializer<EsignatureRequirement> {
	private static final EsignatureRequirementSerializer INSTANCE = new EsignatureRequirementSerializer();

	private EsignatureRequirementSerializer(){}
	public static EsignatureRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(EsignatureRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getTemplateUuid());
		jsonObject.addProperty("templateUuid", requirement.getTemplateUuid());
		jsonObject.addProperty("name", EsignatureRequirement.DEFAULT_NAME);
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		return jsonObject;
	}
}

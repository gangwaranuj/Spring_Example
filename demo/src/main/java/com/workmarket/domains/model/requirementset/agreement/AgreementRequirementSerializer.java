package com.workmarket.domains.model.requirementset.agreement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AgreementRequirementSerializer implements JsonSerializer<AgreementRequirement> {
	private static final AgreementRequirementSerializer INSTANCE = new AgreementRequirementSerializer();

	private AgreementRequirementSerializer(){}
	public static AgreementRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(AgreementRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("allowMultiple", requirement.allowMultiple());

		JsonObject agreementObject = new JsonObject();
		agreementObject.addProperty("name", requirement.getAgreementRequirable().getName());
		agreementObject.addProperty("id", requirement.getAgreementRequirable().getId());
		jsonObject.add("requirable", agreementObject);

		return jsonObject;
	}
}

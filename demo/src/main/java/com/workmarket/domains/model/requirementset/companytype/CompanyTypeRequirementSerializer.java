package com.workmarket.domains.model.requirementset.companytype;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class CompanyTypeRequirementSerializer implements JsonSerializer<CompanyTypeRequirement> {
	private static final CompanyTypeRequirementSerializer INSTANCE = new CompanyTypeRequirementSerializer();

	private CompanyTypeRequirementSerializer(){}
	public static CompanyTypeRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(CompanyTypeRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		JsonObject companyTypeObject = new JsonObject();
		companyTypeObject.addProperty("name", requirement.getCompanyTypeRequirable().getName());
		companyTypeObject.addProperty("id", requirement.getCompanyTypeRequirable().getId());
		jsonObject.add("requirable", companyTypeObject);

		return jsonObject;
	}
}

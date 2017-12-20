package com.workmarket.domains.model.requirementset.country;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class CountryRequirementSerializer implements JsonSerializer<CountryRequirement> {
	private static final CountryRequirementSerializer INSTANCE = new CountryRequirementSerializer();

	private CountryRequirementSerializer(){}
	public static CountryRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(CountryRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		JsonObject companyTypeObject = new JsonObject();
		companyTypeObject.addProperty("name", requirement.getCountryRequirable().getName());
		companyTypeObject.addProperty("id", requirement.getCountryRequirable().getId());
		jsonObject.add("requirable", companyTypeObject);

		return jsonObject;
	}
}

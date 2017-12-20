package com.workmarket.domains.model.requirementset.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TestRequirementSerializer implements JsonSerializer<TestRequirement> {
	private static final TestRequirementSerializer INSTANCE = new TestRequirementSerializer();

	private TestRequirementSerializer(){}
	public static TestRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(TestRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("allowMultiple", requirement.allowMultiple());

		JsonObject testObject = new JsonObject();
		testObject.addProperty("name", requirement.getTestRequirable().getName());
		testObject.addProperty("id", requirement.getTestRequirable().getId());
		jsonObject.add("requirable", testObject);

		return jsonObject;
	}
}

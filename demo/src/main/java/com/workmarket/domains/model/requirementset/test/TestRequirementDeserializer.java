package com.workmarket.domains.model.requirementset.test;

import com.google.gson.*;

import java.lang.reflect.Type;

public class TestRequirementDeserializer implements JsonDeserializer<TestRequirement> {
	private static final TestRequirementDeserializer INSTANCE = new TestRequirementDeserializer();

	private TestRequirementDeserializer(){}
	public static TestRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public TestRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		TestRequirement requirement = new TestRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement test = jsonObject.get("requirable");
		if (test != null && !test.isJsonNull()) {
			requirement.setTestRequirable((TestRequirable) context.deserialize(test, TestRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

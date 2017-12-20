package com.workmarket.domains.model.requirementset.backgroundcheck;

import com.google.gson.*;

import java.lang.reflect.Type;

public class BackgroundCheckRequirementDeserializer implements JsonDeserializer<BackgroundCheckRequirement> {
	private static final BackgroundCheckRequirementDeserializer INSTANCE = new BackgroundCheckRequirementDeserializer();

	private BackgroundCheckRequirementDeserializer(){}
	public static BackgroundCheckRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public BackgroundCheckRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		BackgroundCheckRequirement requirement = new BackgroundCheckRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

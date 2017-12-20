package com.workmarket.domains.model.requirementset.drugtest;

import com.google.gson.*;

import java.lang.reflect.Type;

public class DrugTestRequirementDeserializer implements JsonDeserializer<DrugTestRequirement> {
	private static final DrugTestRequirementDeserializer INSTANCE = new DrugTestRequirementDeserializer();

	private DrugTestRequirementDeserializer(){}
	public static DrugTestRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public DrugTestRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		DrugTestRequirement requirement = new DrugTestRequirement();

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

package com.workmarket.domains.model.requirementset.rating;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class RatingRequirementDeserializer implements JsonDeserializer<RatingRequirement> {
	private static final RatingRequirementDeserializer INSTANCE = new RatingRequirementDeserializer();

	private RatingRequirementDeserializer(){}
	public static RatingRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public RatingRequirement deserialize(
		JsonElement jsonElement,
		Type typeOfT,
		JsonDeserializationContext context) {

		JsonObject jsonObject = jsonElement.getAsJsonObject();
		RatingRequirement requirement = new RatingRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement value = jsonObject.get("value");
		if (value != null && !value.isJsonNull()) {
			requirement.setValue(value.getAsInt());
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

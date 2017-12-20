package com.workmarket.domains.model.requirementset.industry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class IndustryRequirementDeserializer implements JsonDeserializer<IndustryRequirement> {
	private static final IndustryRequirementDeserializer INSTANCE = new IndustryRequirementDeserializer();

	private IndustryRequirementDeserializer(){}
	public static IndustryRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public IndustryRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		IndustryRequirement requirement = new IndustryRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement industry = jsonObject.get("requirable");
		if (industry != null && !industry.isJsonNull()) {
			requirement.setIndustryRequirable((IndustryRequirable) context.deserialize(industry, IndustryRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

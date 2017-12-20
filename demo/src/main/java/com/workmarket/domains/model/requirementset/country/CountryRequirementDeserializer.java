package com.workmarket.domains.model.requirementset.country;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CountryRequirementDeserializer implements JsonDeserializer<CountryRequirement> {
	private static final CountryRequirementDeserializer INSTANCE = new CountryRequirementDeserializer();

	private CountryRequirementDeserializer(){}
	public static CountryRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public CountryRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		CountryRequirement requirement = new CountryRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement country = jsonObject.get("requirable");
		if (country != null && !country.isJsonNull()) {
			requirement.setCountryRequirable((CountryRequirable) context.deserialize(country, CountryRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

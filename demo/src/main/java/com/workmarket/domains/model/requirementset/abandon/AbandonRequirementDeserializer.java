package com.workmarket.domains.model.requirementset.abandon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/19/13
 */
public class AbandonRequirementDeserializer implements JsonDeserializer<AbandonRequirement> {
	private static final AbandonRequirementDeserializer INSTANCE = new AbandonRequirementDeserializer();

	private AbandonRequirementDeserializer(){}
	public static AbandonRequirementDeserializer getInstance() { return INSTANCE; }

	@Override
	public AbandonRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		AbandonRequirement requirement = new AbandonRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		JsonElement maximumAllowed = jsonObject.get("maximumAllowed");
		if (maximumAllowed != null && !maximumAllowed.isJsonNull()) {
			requirement.setMaximumAllowed(maximumAllowed.getAsInt());
		}

		return requirement;
	}
}

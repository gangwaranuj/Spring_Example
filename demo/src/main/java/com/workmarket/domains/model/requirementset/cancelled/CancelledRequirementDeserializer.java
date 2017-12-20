package com.workmarket.domains.model.requirementset.cancelled;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/19/13
 */
public class CancelledRequirementDeserializer implements JsonDeserializer<CancelledRequirement> {
	private static final CancelledRequirementDeserializer INSTANCE = new CancelledRequirementDeserializer();

	private CancelledRequirementDeserializer(){}
	public static CancelledRequirementDeserializer getInstance() { return INSTANCE; }

	@Override
	public CancelledRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		CancelledRequirement requirement = new CancelledRequirement();

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

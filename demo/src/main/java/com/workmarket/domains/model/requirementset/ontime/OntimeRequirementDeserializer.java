package com.workmarket.domains.model.requirementset.ontime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/19/13
 */
public class OntimeRequirementDeserializer implements JsonDeserializer<OntimeRequirement> {
	private static final OntimeRequirementDeserializer INSTANCE = new OntimeRequirementDeserializer();

	private OntimeRequirementDeserializer(){}
	public static OntimeRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public OntimeRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		OntimeRequirement requirement = new OntimeRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		JsonElement minPercent = jsonObject.get("minimumPercentage");
		if (minPercent != null && !minPercent.isJsonNull()) {
			requirement.setMinimumPercentage(minPercent.getAsInt());
		}

		return requirement;
	}
}

package com.workmarket.domains.model.requirementset.deliverableontime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

/**
 * Created by rahul on 7/2/14
 */
public class DeliverableOnTimeRequirementDeserializer implements JsonDeserializer<DeliverableOnTimeRequirement> {
	private static final DeliverableOnTimeRequirementDeserializer INSTANCE = new DeliverableOnTimeRequirementDeserializer();

	private DeliverableOnTimeRequirementDeserializer() {
	}

	public static DeliverableOnTimeRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public DeliverableOnTimeRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		DeliverableOnTimeRequirement requirement = new DeliverableOnTimeRequirement();

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

package com.workmarket.domains.model.requirementset.paid;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/23/13
 */
public class PaidRequirementDeserializer implements JsonDeserializer<PaidRequirement> {
	private static final PaidRequirementDeserializer INSTANCE = new PaidRequirementDeserializer();

	private PaidRequirementDeserializer(){}
	public static PaidRequirementDeserializer getInstance() { return INSTANCE; }

	@Override
	public PaidRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		PaidRequirement requirement = new PaidRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		JsonElement minAssignments = jsonObject.get("minimumAssignments");
		if (minAssignments != null && !minAssignments.isJsonNull()) {
			requirement.setMinimumAssignments(minAssignments.getAsInt());
		}

		return requirement;
	}
}

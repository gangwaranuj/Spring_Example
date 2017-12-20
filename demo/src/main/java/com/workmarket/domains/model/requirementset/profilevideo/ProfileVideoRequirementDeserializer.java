package com.workmarket.domains.model.requirementset.profilevideo;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class ProfileVideoRequirementDeserializer implements JsonDeserializer<ProfileVideoRequirement> {

	private static final ProfileVideoRequirementDeserializer INSTANCE = new ProfileVideoRequirementDeserializer();

	private ProfileVideoRequirementDeserializer(){}
	public static ProfileVideoRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public ProfileVideoRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		ProfileVideoRequirement requirement = new ProfileVideoRequirement();

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

package com.workmarket.domains.model.requirementset.profilepicture;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class ProfilePictureRequirementDeserializer implements JsonDeserializer<ProfilePictureRequirement> {

	private static final ProfilePictureRequirementDeserializer INSTANCE = new ProfilePictureRequirementDeserializer();

	private ProfilePictureRequirementDeserializer(){}
	public static ProfilePictureRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public ProfilePictureRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		ProfilePictureRequirement requirement = new ProfilePictureRequirement();

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

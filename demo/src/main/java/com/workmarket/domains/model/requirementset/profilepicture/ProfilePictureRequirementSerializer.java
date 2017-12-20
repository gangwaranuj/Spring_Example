package com.workmarket.domains.model.requirementset.profilepicture;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ProfilePictureRequirementSerializer implements JsonSerializer<ProfilePictureRequirement> {

	private static final ProfilePictureRequirementSerializer INSTANCE = new ProfilePictureRequirementSerializer();

	private ProfilePictureRequirementSerializer(){}
	public static ProfilePictureRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(ProfilePictureRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("name", ProfilePictureRequirement.DEFAULT_NAME);
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		return jsonObject;
	}
}

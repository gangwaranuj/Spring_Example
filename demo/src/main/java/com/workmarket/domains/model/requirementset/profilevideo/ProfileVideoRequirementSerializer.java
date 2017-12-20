package com.workmarket.domains.model.requirementset.profilevideo;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ProfileVideoRequirementSerializer implements JsonSerializer<ProfileVideoRequirement> {

	private static final ProfileVideoRequirementSerializer INSTANCE = new ProfileVideoRequirementSerializer();

	private ProfileVideoRequirementSerializer(){}
	public static ProfileVideoRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(ProfileVideoRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("name", ProfileVideoRequirement.DEFAULT_NAME);
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		return jsonObject;
	}
}

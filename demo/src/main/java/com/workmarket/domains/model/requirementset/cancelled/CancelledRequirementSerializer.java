package com.workmarket.domains.model.requirementset.cancelled;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/19/13
 */
public class CancelledRequirementSerializer implements JsonSerializer<CancelledRequirement> {
	private static final CancelledRequirementSerializer INSTANCE = new CancelledRequirementSerializer();

	private CancelledRequirementSerializer(){}
	public static CancelledRequirementSerializer getInstance() { return INSTANCE; }

	@Override
	public JsonElement serialize(CancelledRequirement requirement, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("maximumAllowed", requirement.getMaximumAllowed());
		jsonObject.addProperty("name", requirement.getName());

		return jsonObject;
	}
}

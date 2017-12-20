package com.workmarket.domains.model.requirementset.paid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/23/13
 */
public class PaidRequirementSerializer implements JsonSerializer<PaidRequirement> {
	private static final PaidRequirementSerializer INSTANCE = new PaidRequirementSerializer();

	private PaidRequirementSerializer(){}
	public static PaidRequirementSerializer getInstance() { return INSTANCE; }

	@Override
	public JsonElement serialize(PaidRequirement requirement, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("minimumAssignments", requirement.getMinimumAssignments());
		jsonObject.addProperty("name", requirement.getName());

		return jsonObject;
	}
}

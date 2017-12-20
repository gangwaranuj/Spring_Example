package com.workmarket.domains.model.requirementset.deliverableontime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by rahul on 7/2/14
 */
public class DeliverableOnTimeRequirementSerializer implements JsonSerializer<DeliverableOnTimeRequirement> {
	private static final DeliverableOnTimeRequirementSerializer INSTANCE = new DeliverableOnTimeRequirementSerializer();

	private DeliverableOnTimeRequirementSerializer(){
	}

	public static DeliverableOnTimeRequirementSerializer getInstance() { return INSTANCE; }

	@Override
	public JsonElement serialize(DeliverableOnTimeRequirement requirement, Type typeOfSrc, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("minimumPercentage", requirement.getMinimumPercentage());
		jsonObject.addProperty("name", requirement.getName());

		return jsonObject;
	}
}

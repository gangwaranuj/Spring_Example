package com.workmarket.domains.model.requirementset.availability;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AvailabilityRequirementSerializer implements JsonSerializer<AvailabilityRequirement> {
	private static final AvailabilityRequirementSerializer INSTANCE = new AvailabilityRequirementSerializer();

	private AvailabilityRequirementSerializer(){}
	public static AvailabilityRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(AvailabilityRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		JsonObject weekdayObject = new JsonObject();
		weekdayObject.addProperty("name", requirement.getName());
		weekdayObject.addProperty("id", requirement.getWeekdayRequirable().getId());
		jsonObject.add("requirable", weekdayObject);

		jsonObject.addProperty("dayOfWeek", requirement.getDayOfWeek());
		jsonObject.addProperty("fromTime", String.valueOf(requirement.getFromTime()));
		jsonObject.addProperty("toTime", String.valueOf(requirement.getToTime()));

		return jsonObject;
	}
}

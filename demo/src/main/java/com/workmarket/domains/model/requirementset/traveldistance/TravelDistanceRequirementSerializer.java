package com.workmarket.domains.model.requirementset.traveldistance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TravelDistanceRequirementSerializer implements JsonSerializer<TravelDistanceRequirement> {
	private static final TravelDistanceRequirementSerializer INSTANCE = new TravelDistanceRequirementSerializer();

	private TravelDistanceRequirementSerializer(){}
	public static TravelDistanceRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(TravelDistanceRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("distance", requirement.getDistance());
		jsonObject.addProperty("address", requirement.getAddress());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("latitude", requirement.getLatitude());
		jsonObject.addProperty("longitude", requirement.getLongitude());
		jsonObject.addProperty("name", String.format(
			TravelDistanceRequirement.NAME_TEMPLATE,
			requirement.getDistance(),
			requirement.getAddress()
		));

		return jsonObject;
	}
}

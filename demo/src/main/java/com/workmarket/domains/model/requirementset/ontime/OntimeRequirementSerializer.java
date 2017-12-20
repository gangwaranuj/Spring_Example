package com.workmarket.domains.model.requirementset.ontime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/19/13
 */
public class OntimeRequirementSerializer implements JsonSerializer<OntimeRequirement> {
	private static final OntimeRequirementSerializer INSTANCE = new OntimeRequirementSerializer();

	private OntimeRequirementSerializer(){}
	public static OntimeRequirementSerializer getInstance() { return INSTANCE; }

	@Override
	public JsonElement serialize(OntimeRequirement requirement, Type typeOfSrc, JsonSerializationContext jsc) {
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

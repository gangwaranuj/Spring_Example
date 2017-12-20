package com.workmarket.domains.model.requirementset.resourcetype;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ResourceTypeRequirementSerializer implements JsonSerializer<ResourceTypeRequirement> {
	private static final ResourceTypeRequirementSerializer INSTANCE = new ResourceTypeRequirementSerializer();

	private ResourceTypeRequirementSerializer(){}
	public static ResourceTypeRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(ResourceTypeRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		JsonObject resourceTypeObject = new JsonObject();
		resourceTypeObject.addProperty("name", requirement.getResourceTypeRequirable().getName());
		resourceTypeObject.addProperty("id", requirement.getResourceTypeRequirable().getId());
		jsonObject.add("requirable", resourceTypeObject);

		return jsonObject;
	}
}

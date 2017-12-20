package com.workmarket.domains.model.requirementset.resourcetype;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ResourceTypeRequirementDeserializer implements JsonDeserializer<ResourceTypeRequirement> {
	private static final ResourceTypeRequirementDeserializer INSTANCE = new ResourceTypeRequirementDeserializer();

	private ResourceTypeRequirementDeserializer(){}
	public static ResourceTypeRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public ResourceTypeRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		ResourceTypeRequirement requirement = new ResourceTypeRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement resourceType = jsonObject.get("requirable");
		if (resourceType != null && !resourceType.isJsonNull()) {
			requirement.setResourceTypeRequirable((ResourceTypeRequirable) context.deserialize(resourceType, ResourceTypeRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

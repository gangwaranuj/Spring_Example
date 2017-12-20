package com.workmarket.domains.model.requirementset.esignature;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class EsignatureRequirementDeserializer implements JsonDeserializer<EsignatureRequirement> {
	private static final EsignatureRequirementDeserializer INSTANCE = new EsignatureRequirementDeserializer();

	private EsignatureRequirementDeserializer(){}
	public static EsignatureRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public EsignatureRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		final JsonObject jsonObject = jsonElement.getAsJsonObject();
		final EsignatureRequirement requirement = new EsignatureRequirement();

		final JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setTemplateUuid(id.getAsString());
		}

		final JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		final JsonElement templateUuid = jsonObject.get("templateUuid");
		if (templateUuid != null && !templateUuid.isJsonNull()) {
			requirement.setTemplateUuid(templateUuid.getAsString());
		}

		return requirement;
	}
}

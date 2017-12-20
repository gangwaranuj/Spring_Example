package com.workmarket.domains.model.requirementset.agreement;

import com.google.gson.*;

import java.lang.reflect.Type;

public class AgreementRequirementDeserializer implements JsonDeserializer<AgreementRequirement> {
	private static final AgreementRequirementDeserializer INSTANCE = new AgreementRequirementDeserializer();

	private AgreementRequirementDeserializer(){}
	public static AgreementRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public AgreementRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		AgreementRequirement requirement = new AgreementRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement agreement = jsonObject.get("requirable");
		if (agreement != null && !agreement.isJsonNull()) {
			requirement.setAgreementRequirable((AgreementRequirable) context.deserialize(agreement, AgreementRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

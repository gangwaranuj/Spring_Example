package com.workmarket.domains.model.requirementset.companytype;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CompanyTypeRequirementDeserializer implements JsonDeserializer<CompanyTypeRequirement> {
	private static final CompanyTypeRequirementDeserializer INSTANCE = new CompanyTypeRequirementDeserializer();

	private CompanyTypeRequirementDeserializer(){}
	public static CompanyTypeRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public CompanyTypeRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		CompanyTypeRequirement requirement = new CompanyTypeRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement companyType = jsonObject.get("requirable");
		if (companyType != null && !companyType.isJsonNull()) {
			requirement.setCompanyTypeRequirable((CompanyTypeRequirable) context.deserialize(companyType, CompanyTypeRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

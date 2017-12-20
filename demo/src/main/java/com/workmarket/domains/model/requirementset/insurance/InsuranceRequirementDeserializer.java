package com.workmarket.domains.model.requirementset.insurance;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class InsuranceRequirementDeserializer implements JsonDeserializer<InsuranceRequirement> {
	private static final InsuranceRequirementDeserializer INSTANCE = new InsuranceRequirementDeserializer();

	private InsuranceRequirementDeserializer(){}
	public static InsuranceRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public InsuranceRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		InsuranceRequirement requirement = new InsuranceRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement minimumCoverage = jsonObject.get("minimumCoverage");
		if (minimumCoverage != null && !minimumCoverage.isJsonNull()) {
			requirement.setMinimumCoverageAmount(minimumCoverage.getAsBigDecimal());
		}

		JsonElement insurance = jsonObject.get("requirable");
		if (insurance != null && !insurance.isJsonNull()) {
			requirement.setInsuranceRequirable((InsuranceRequirable) context.deserialize(insurance, InsuranceRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		JsonElement notifyOnExpiry = jsonObject.get("notifyOnExpiry");
		if (notifyOnExpiry != null && !notifyOnExpiry.isJsonNull()) {
			requirement.setNotifyOnExpiry(notifyOnExpiry.getAsBoolean());
		}

		JsonElement removeMembershipOnExpiry = jsonObject.get("removeMembershipOnExpiry");
		if (removeMembershipOnExpiry != null && !removeMembershipOnExpiry.isJsonNull()) {
			requirement.setRemoveMembershipOnExpiry(removeMembershipOnExpiry.getAsBoolean());
		}

		return requirement;
	}
}

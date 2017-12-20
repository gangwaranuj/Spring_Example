package com.workmarket.domains.model.requirementset.certification;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CertificationRequirementDeserializer implements JsonDeserializer<CertificationRequirement> {
	private static final CertificationRequirementDeserializer INSTANCE = new CertificationRequirementDeserializer();

	private CertificationRequirementDeserializer(){}
	public static CertificationRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public CertificationRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		CertificationRequirement requirement = new CertificationRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement certification = jsonObject.get("requirable");
		if (certification != null && !certification.isJsonNull()) {
			requirement.setCertificationRequirable((CertificationRequirable) context.deserialize(certification, CertificationRequirable.class));
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

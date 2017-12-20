package com.workmarket.domains.model.requirementset.license;

import com.google.gson.*;

import java.lang.reflect.Type;

public class LicenseRequirementDeserializer implements JsonDeserializer<LicenseRequirement> {
	private static final LicenseRequirementDeserializer INSTANCE = new LicenseRequirementDeserializer();

	private LicenseRequirementDeserializer(){}
	public static LicenseRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public LicenseRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		LicenseRequirement requirement = new LicenseRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement license = jsonObject.get("requirable");
		if (license != null && !license.isJsonNull()) {
			requirement.setLicenseRequirable((LicenseRequirable) context.deserialize(license, LicenseRequirable.class));
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

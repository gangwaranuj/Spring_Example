package com.workmarket.domains.model.requirementset.license;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class LicenseRequirementSerializer implements JsonSerializer<LicenseRequirement> {
	private static final LicenseRequirementSerializer INSTANCE = new LicenseRequirementSerializer();

	private LicenseRequirementSerializer(){}
	public static LicenseRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(LicenseRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("allowMultiple", requirement.allowMultiple());
		jsonObject.addProperty("notifyOnExpiry", requirement.isNotifyOnExpiry());
		jsonObject.addProperty("removeMembershipOnExpiry", requirement.isRemoveMembershipOnExpiry());

		JsonObject licenseObject = new JsonObject();
		licenseObject.addProperty("name", requirement.getLicenseRequirable().getName());
		licenseObject.addProperty("state", requirement.getLicenseRequirable().getState());
		licenseObject.addProperty("id", requirement.getLicenseRequirable().getId());
		jsonObject.add("requirable", licenseObject);

		return jsonObject;
	}
}

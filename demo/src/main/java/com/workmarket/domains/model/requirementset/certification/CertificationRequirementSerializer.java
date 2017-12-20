package com.workmarket.domains.model.requirementset.certification;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class CertificationRequirementSerializer implements JsonSerializer<CertificationRequirement> {
	private static final CertificationRequirementSerializer INSTANCE = new CertificationRequirementSerializer();

	private CertificationRequirementSerializer(){}
	public static CertificationRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(CertificationRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("allowMultiple", requirement.allowMultiple());
		jsonObject.addProperty("notifyOnExpiry", requirement.isNotifyOnExpiry());
		jsonObject.addProperty("removeMembershipOnExpiry", requirement.isRemoveMembershipOnExpiry());

		JsonObject certificationObject = new JsonObject();
		certificationObject.addProperty("name", requirement.getCertificationRequirable().getName());
		certificationObject.addProperty("id", requirement.getCertificationRequirable().getId());
		jsonObject.add("requirable", certificationObject);

		return jsonObject;
	}
}

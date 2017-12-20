package com.workmarket.domains.model.requirementset.insurance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.NumberFormat;

public class InsuranceRequirementSerializer implements JsonSerializer<InsuranceRequirement> {
	private static final InsuranceRequirementSerializer INSTANCE = new InsuranceRequirementSerializer();

	private InsuranceRequirementSerializer(){}
	public static InsuranceRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(InsuranceRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("minimumCoverage", requirement.getMinimumCoverageAmount());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("allowMultiple", requirement.allowMultiple());
		jsonObject.addProperty("notifyOnExpiry", requirement.isNotifyOnExpiry());
		jsonObject.addProperty("removeMembershipOnExpiry", requirement.isRemoveMembershipOnExpiry());

		JsonObject insuranceObject = new JsonObject();
		if (requirement.getMinimumCoverageAmount().compareTo(BigDecimal.ZERO) > 0) {
			insuranceObject.addProperty("name", String.format(
				InsuranceRequirement.NAME_TEMPLATE,
				requirement.getInsuranceRequirable().getName(),
				NumberFormat.getInstance().format(requirement.getMinimumCoverageAmount())
			));
		} else {
			insuranceObject.addProperty("name", requirement.getInsuranceRequirable().getName());
		}

		insuranceObject.addProperty("id", requirement.getInsuranceRequirable().getId());
		jsonObject.add("requirable", insuranceObject);

		return jsonObject;
	}
}

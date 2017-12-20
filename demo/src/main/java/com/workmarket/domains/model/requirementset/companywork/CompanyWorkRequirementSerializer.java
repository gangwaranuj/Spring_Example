package com.workmarket.domains.model.requirementset.companywork;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/24/13
 */
public class CompanyWorkRequirementSerializer implements JsonSerializer<CompanyWorkRequirement> {
	private static final CompanyWorkRequirementSerializer INSTANCE = new CompanyWorkRequirementSerializer();

	private CompanyWorkRequirementSerializer(){}
	public static CompanyWorkRequirementSerializer getInstance() { return INSTANCE; }

	@Override
	public JsonElement serialize(CompanyWorkRequirement requirement, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("minimumWorkCount", requirement.getMinimumWorkCount());
		jsonObject.addProperty("name", requirement.getName());

		JsonObject agreementObject = new JsonObject();
		agreementObject.addProperty("id", requirement.getCompanyWorkRequirable().getId());
		agreementObject.addProperty("name", requirement.getCompanyWorkRequirable().getName());
		jsonObject.add("requirable", agreementObject);

		return jsonObject;
	}
}

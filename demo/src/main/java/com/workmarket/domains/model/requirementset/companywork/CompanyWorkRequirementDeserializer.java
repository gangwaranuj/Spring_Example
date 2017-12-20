package com.workmarket.domains.model.requirementset.companywork;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/24/13
 */
public class CompanyWorkRequirementDeserializer implements JsonDeserializer<CompanyWorkRequirement> {
	private static final CompanyWorkRequirementDeserializer INSTANCE = new CompanyWorkRequirementDeserializer();

	private CompanyWorkRequirementDeserializer(){}
	public static CompanyWorkRequirementDeserializer getInstance() { return INSTANCE; }

	@Override
	public CompanyWorkRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		CompanyWorkRequirement requirement = new CompanyWorkRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement company = jsonObject.get("requirable");
		if (company != null && !company.isJsonNull()) {
			requirement.setCompanyWorkRequirable((CompanyWorkRequirable) context.deserialize(company, CompanyWorkRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		JsonElement minimumWorkCount = jsonObject.get("minimumWorkCount");
		if (minimumWorkCount != null && !minimumWorkCount.isJsonNull()) {
			requirement.setMinimumWorkCount(minimumWorkCount.getAsInt());
		}

		return requirement;
	}
}

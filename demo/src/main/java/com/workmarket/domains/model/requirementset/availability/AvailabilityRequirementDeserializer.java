package com.workmarket.domains.model.requirementset.availability;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class AvailabilityRequirementDeserializer implements JsonDeserializer<AvailabilityRequirement> {
	private static final AvailabilityRequirementDeserializer INSTANCE = new AvailabilityRequirementDeserializer();

	private AvailabilityRequirementDeserializer(){}
	public static AvailabilityRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public AvailabilityRequirement deserialize(
		JsonElement jsonElement,
		Type typeOfT,
		JsonDeserializationContext context) {

		JsonObject jsonObject = jsonElement.getAsJsonObject();
		AvailabilityRequirement requirement = new AvailabilityRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement weekday = jsonObject.get("requirable");
		if (weekday != null && !weekday.isJsonNull()) {
			requirement.setWeekdayRequirable((WeekdayRequirable) context.deserialize(weekday, WeekdayRequirable.class));
		}

		JsonElement fromTime = jsonObject.get("fromTime");
		if (fromTime != null && !fromTime.isJsonNull()) {
			requirement.setFromTime(fromTime.getAsString());
		}

		JsonElement toTime = jsonObject.get("toTime");
		if (toTime != null && !toTime.isJsonNull()) {
			requirement.setToTime(toTime.getAsString());
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}

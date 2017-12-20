package com.workmarket.domains.model.requirementset.traveldistance;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class TravelDistanceRequirementDeserializer implements JsonDeserializer<TravelDistanceRequirement> {
		private static final TravelDistanceRequirementDeserializer INSTANCE = new TravelDistanceRequirementDeserializer();

		private TravelDistanceRequirementDeserializer(){}
		public static TravelDistanceRequirementDeserializer getInstance() {
			return INSTANCE;
		}

		@Override
		public TravelDistanceRequirement deserialize(
			JsonElement jsonElement,
			Type typeOfT,
			JsonDeserializationContext context) {

			JsonObject jsonObject = jsonElement.getAsJsonObject();
			TravelDistanceRequirement requirement = new TravelDistanceRequirement();

			JsonElement id = jsonObject.get("id");
			if (id != null && !id.isJsonNull()) {
				requirement.setId(id.getAsLong());
			}

			JsonElement longitude = jsonObject.get("longitude");
			if (longitude != null && !longitude.isJsonNull()) {
				requirement.setLongitude(longitude.getAsDouble());
			}

			JsonElement latitude = jsonObject.get("latitude");
			if (latitude != null && !latitude.isJsonNull()) {
				requirement.setLatitude(latitude.getAsDouble());
			}

			JsonElement distance = jsonObject.get("distance");
			if (distance != null && !distance.isJsonNull()) {
				requirement.setDistance(distance.getAsLong());
			}

			JsonElement address = jsonObject.get("address");
			if (address != null && !address.isJsonNull()) {
				requirement.setAddress(address.getAsString());
			}

			JsonElement mandatory = jsonObject.get("mandatory");
			if (mandatory != null && !mandatory.isJsonNull()) {
				requirement.setMandatory(mandatory.getAsBoolean());
			}

			return requirement;
		}
	}

package com.workmarket.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.license.License;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class LicenseDeserializer implements JsonDeserializer<License> {

	private static final LicenseDeserializer INSTANCE = new LicenseDeserializer();

	private LicenseDeserializer() {
	}

	public static LicenseDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public License deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		License license = new License();
		JsonElement field = jsonObject.get("name");
		if (field != null && !field.isJsonNull()) {
			license.setName(field.getAsString());
		}
		field = jsonObject.get("state");
		if (field != null && !field.isJsonNull()) {
			license.setState(field.getAsString());
		}
		field = jsonObject.get("id");
		if (field != null && !field.isJsonNull()) {
			license.setId(field.getAsLong());
		}
		return license;
	}
}

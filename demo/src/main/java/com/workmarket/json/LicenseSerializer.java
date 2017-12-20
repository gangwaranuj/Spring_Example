package com.workmarket.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.license.License;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class LicenseSerializer implements JsonSerializer<License> {

	private static final LicenseSerializer INSTANCE = new LicenseSerializer();

	private LicenseSerializer() {}

	public static LicenseSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(License license, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("name", license.getName());
		jsonObject.addProperty("state", license.getState());
		jsonObject.addProperty("id", license.getId());
		return jsonObject;
	}
}

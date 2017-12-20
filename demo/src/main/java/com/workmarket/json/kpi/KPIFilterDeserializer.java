package com.workmarket.json.kpi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIReportFilter;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Author: rocio
 */
public class KPIFilterDeserializer implements JsonDeserializer<Filter> {

	private static final KPIFilterDeserializer INSTANCE = new KPIFilterDeserializer();

	private KPIFilterDeserializer() {
	}

	public static KPIFilterDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public Filter deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		Filter filter = new Filter();

		JsonElement field = jsonObject.get("name");
		if (field != null && !field.isJsonNull()) {
			filter.setName(KPIReportFilter.valueOf(field.getAsString()));
		}

		field = jsonObject.get("values");
		if (field != null && !field.isJsonNull()) {
			filter.setValues(Arrays.asList((String[]) jsonDeserializationContext.deserialize(field, String[].class)));
		}
		return filter;
	}
}

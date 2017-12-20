package com.workmarket.json.kpi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.kpi.Filter;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Author: rocio
 */
public class KPIFilterSerializer implements JsonSerializer<Filter> {

	private static final KPIFilterSerializer INSTANCE = new KPIFilterSerializer();

	private KPIFilterSerializer() {}

	public static KPIFilterSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(Filter filter, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("name", filter.getName().toString());
		if (isNotEmpty(filter.getValues())) {
			jsonObject.add("values", jsonSerializationContext.serialize(filter.getValues(), ArrayList.class));
		}
		return jsonObject;
	}
}

package com.workmarket.json.kpi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.kpi.DataPoint;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class DataPointSerializer implements JsonSerializer<DataPoint> {

	private static final DataPointSerializer INSTANCE = new DataPointSerializer();

	private DataPointSerializer() {}

	public static DataPointSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(DataPoint dataPoint, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("x", dataPoint.getX());
		jsonObject.addProperty("y", dataPoint.getY());
		jsonObject.addProperty("inProgressPeriod", dataPoint.isInProgressPeriod());
		jsonObject.addProperty("trendingUp", dataPoint.isTrendingUp());
		return jsonObject;
	}
}

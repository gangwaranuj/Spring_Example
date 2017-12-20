package com.workmarket.json.kpi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.kpi.DataPoint;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class DataPointDeserializer implements JsonDeserializer<DataPoint> {

	private static final DataPointDeserializer INSTANCE = new DataPointDeserializer();

	private DataPointDeserializer() {
	}

	public static DataPointDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public DataPoint deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		DataPoint dataPoint = new DataPoint();

		JsonElement field = jsonObject.get("x");
		if (field != null && !field.isJsonNull()) {
			dataPoint.setX(field.getAsLong());
		}

		field = jsonObject.get("y");
		if (field != null && !field.isJsonNull()) {
			dataPoint.setY(field.getAsDouble());
		}

		field = jsonObject.get("inProgressPeriod");
		if (field != null && !field.isJsonNull()) {
			dataPoint.setInProgressPeriod(field.getAsBoolean());
		}

		field = jsonObject.get("trendingUp");
		if (field != null && !field.isJsonNull()) {
			dataPoint.setTrendingUp(field.getAsBoolean());
		}
		return dataPoint;
	}
}

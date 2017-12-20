package com.workmarket.json.kpi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.KPIChartResponse;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Author: rocio
 */
public class KPIChartResponseDeserializer implements JsonDeserializer<KPIChartResponse> {

	private static final KPIChartResponseDeserializer INSTANCE = new KPIChartResponseDeserializer();

	private KPIChartResponseDeserializer() {
	}

	public static KPIChartResponseDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public KPIChartResponse deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		KPIChartResponse kpiChartResponse = new KPIChartResponse();

		JsonElement field = jsonObject.get("xAxisLabel");
		if (field != null && !field.isJsonNull()) {
			kpiChartResponse.setXAxisLabel(field.getAsString());
		}

		field = jsonObject.get("yAxisLabel");
		if (field != null && !field.isJsonNull()) {
			kpiChartResponse.setYAxisLabel(field.getAsString());
		}

		field = jsonObject.get("chartData");
		if (field != null && !field.isJsonNull()) {
			kpiChartResponse.setChartData(Arrays.asList((DataPoint[]) jsonDeserializationContext.deserialize(field, DataPoint[].class)));
		}

		return kpiChartResponse;
	}
}

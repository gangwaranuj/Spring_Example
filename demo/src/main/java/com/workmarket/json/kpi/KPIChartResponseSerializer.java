package com.workmarket.json.kpi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.kpi.KPIChartResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Author: rocio
 */
public class KPIChartResponseSerializer implements JsonSerializer<KPIChartResponse> {

	private static final KPIChartResponseSerializer INSTANCE = new KPIChartResponseSerializer();

	private KPIChartResponseSerializer() {}

	public static KPIChartResponseSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(KPIChartResponse kpiChartResponse, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("xAxisLabel", kpiChartResponse.getXAxisLabel());
		jsonObject.addProperty("yAxisLabel", kpiChartResponse.getYAxisLabel());
		if (isNotEmpty(kpiChartResponse.getChartData())) {
			jsonObject.add("chartData", jsonSerializationContext.serialize(kpiChartResponse.getChartData(), ArrayList.class));
		}
		return jsonObject;
	}
}

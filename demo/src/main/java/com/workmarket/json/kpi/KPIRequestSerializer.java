package com.workmarket.json.kpi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.data.report.kpi.KPIRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Author: rocio
 */
public class KPIRequestSerializer implements JsonSerializer<KPIRequest> {

	private static final KPIRequestSerializer INSTANCE = new KPIRequestSerializer();

	private KPIRequestSerializer() {}

	public static KPIRequestSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(KPIRequest kpiRequest, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		if (kpiRequest.getReportType() != null) {
			jsonObject.addProperty("reportType", kpiRequest.getReportType().toString());
		}
		if (kpiRequest.getAggregateInterval() != null) {
			jsonObject.addProperty("aggregateInterval", kpiRequest.getAggregateInterval().toString());
		}
		jsonObject.add("workStatusType", jsonSerializationContext.serialize(kpiRequest.getWorkStatusType()));
		if (isNotEmpty(kpiRequest.getFilters())) {
			jsonObject.add("filters", jsonSerializationContext.serialize(kpiRequest.getFilters(), ArrayList.class));
		}
		return jsonObject;
	}
}

package com.workmarket.json.kpi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.domains.model.kpi.KPIReportType;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Author: rocio
 */
public class KPIRequestDeserializer implements JsonDeserializer<KPIRequest> {

	private static final KPIRequestDeserializer INSTANCE = new KPIRequestDeserializer();

	private KPIRequestDeserializer() {}

	public static KPIRequestDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public KPIRequest deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		KPIRequest kpiRequest = new KPIRequest();

		JsonElement field = jsonObject.get("reportType");
		if (field != null && !field.isJsonNull()) {
			kpiRequest.setReportType(KPIReportType.valueOf(field.getAsString()));
		}

		field = jsonObject.get("aggregateInterval");
		if (field != null && !field.isJsonNull()) {
			kpiRequest.setAggregateInterval(KPIReportAggregateInterval.valueOf(field.getAsString()));
		}

		field = jsonObject.get("from");
		if (field != null && !field.isJsonNull()) {
			kpiRequest.setFrom((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}

		field = jsonObject.get("to");
		if (field != null && !field.isJsonNull()) {
			kpiRequest.setTo((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}

		field = jsonObject.get("workStatusType");
		if (field != null && !field.isJsonNull()) {
			kpiRequest.setWorkStatusType((WorkStatusType) jsonDeserializationContext.deserialize(field, WorkStatusType.class));
		}

		field = jsonObject.get("filters");
		if (field != null && !field.isJsonNull()) {
			kpiRequest.setFilters(Arrays.asList((Filter[]) jsonDeserializationContext.deserialize(field, Filter[].class)));
		}
		return kpiRequest;
	}
}

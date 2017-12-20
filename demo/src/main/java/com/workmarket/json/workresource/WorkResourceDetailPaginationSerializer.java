package com.workmarket.json.workresource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class WorkResourceDetailPaginationSerializer implements JsonSerializer<WorkResourceDetailPagination> {

	private static final WorkResourceDetailPaginationSerializer INSTANCE = new WorkResourceDetailPaginationSerializer();

	private WorkResourceDetailPaginationSerializer() {}

	public static WorkResourceDetailPaginationSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(WorkResourceDetailPagination pagination, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("rowCount", pagination.getRowCount());
		jsonObject.addProperty("startRow", pagination.getStartRow());
		jsonObject.addProperty("resultsLimit", pagination.getResultsLimit());
		jsonObject.addProperty("sortColumn", pagination.getSortColumn());
		jsonObject.addProperty("sortDirection", pagination.getSortDirection().toString());
		jsonObject.addProperty("includeApplyNegotiation", pagination.isIncludeApplyNegotiation());
		jsonObject.addProperty("includeNotes", pagination.isIncludeNotes());
		jsonObject.addProperty("includeLabels", pagination.isIncludeLabels());

		if (isNotEmpty(pagination.getResults())) {
			jsonObject.add("results", jsonSerializationContext.serialize(pagination.getResults(), ArrayList.class));
		}
		return jsonObject;
	}
}

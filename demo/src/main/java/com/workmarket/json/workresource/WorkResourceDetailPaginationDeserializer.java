package com.workmarket.json.workresource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.Pagination;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Author: rocio
 */
public class WorkResourceDetailPaginationDeserializer implements JsonDeserializer<WorkResourceDetailPagination> {

	private static final WorkResourceDetailPaginationDeserializer INSTANCE = new WorkResourceDetailPaginationDeserializer();

	private WorkResourceDetailPaginationDeserializer() {
	}

	public static WorkResourceDetailPaginationDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public WorkResourceDetailPagination deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		WorkResourceDetailPagination pagination = new WorkResourceDetailPagination();

		JsonElement field = jsonObject.get("rowCount");
		if (field != null && !field.isJsonNull()) {
			pagination.setRowCount(field.getAsInt());
		}
		field = jsonObject.get("startRow");
		if (field != null && !field.isJsonNull()) {
			pagination.setStartRow(field.getAsInt());
		}
		field = jsonObject.get("resultsLimit");
		if (field != null && !field.isJsonNull()) {
			pagination.setResultsLimit(field.getAsInt());
		}
		field = jsonObject.get("sortColumn");
		if (field != null && !field.isJsonNull()) {
			pagination.setSortColumn(field.getAsString());
		}
		field = jsonObject.get("sortDirection");
		if (field != null && !field.isJsonNull()) {
			pagination.setSortDirection(Pagination.SORT_DIRECTION.valueOf(field.getAsString()));
		}
		field = jsonObject.get("includeApplyNegotiation");
		if (field != null && !field.isJsonNull()) {
			pagination.setIncludeApplyNegotiation(field.getAsBoolean());
		}
		field = jsonObject.get("includeNotes");
		if (field != null && !field.isJsonNull()) {
			pagination.setIncludeNotes(field.getAsBoolean());
		}
		field = jsonObject.get("includeLabels");
		if (field != null && !field.isJsonNull()) {
			pagination.setIncludeLabels(field.getAsBoolean());
		}
		field = jsonObject.get("results");
		if (field != null && !field.isJsonNull()) {
			pagination.setResults(Arrays.asList((WorkResourceDetail[]) jsonDeserializationContext.deserialize(field, WorkResourceDetail[].class)));
		}
		return pagination;
	}
}

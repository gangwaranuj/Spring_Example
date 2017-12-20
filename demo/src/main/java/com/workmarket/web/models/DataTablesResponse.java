package com.workmarket.web.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.Pagination;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.web.serializers.XSSEscapingSerializer;

import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataTablesResponse<T,M> implements PaginatableHttpResponse<T,M> {
	@JsonProperty(value = "iTotalRecords")
	private int totalRecords;

	@JsonProperty(value = "iTotalDisplayRecords")
	private int totalDisplayRecords;

	@JsonProperty(value = "sEcho")
	private String echo;

	@JsonProperty(value = "aaData")
	@JsonSerialize(using = XSSEscapingSerializer.class)
	private List<T> data = Lists.newArrayList();

	@JsonProperty(value = "aMeta")
	private List<M> meta = Lists.newArrayList();

	@JsonProperty(value = "aFilters")
	private Map<String,Object> filters = Maps.newHashMap();

	@JsonProperty(value = "responseMeta")
	private Map<String,Object> responseMeta = Maps.newHashMap();

	public void setEcho(String echo) {
		this.echo = echo;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public void setTotalDisplayRecords(int totalDisplayRecords) {
		this.totalDisplayRecords = totalDisplayRecords;
	}
	public void setResponseMeta(Map<String, Object> responseMeta) {
		this.responseMeta = responseMeta;
	}

	@Override
	public void addRow(T data) {
		this.addRow(data, null);
	}

	@Override
	public void addRow(T data, M meta) {
		this.data.add(data);
		this.meta.add(meta);
	}

	public void addFilter(String key, Object value) {
		this.filters.put(key, value);
	}

	public static <T,M> DataTablesResponse<T,M> newInstance(DataTablesRequest request) {
		DataTablesResponse<T,M> response = new DataTablesResponse<T,M>();
		response.setEcho(request.getEcho());
		return response;
	}

	public static <T,M> DataTablesResponse<T,M> newInstance(DataTablesRequest request, Pagination<?> pagination) {
		return newInstance(request, pagination.getRowCount());
	}

	public static <T,M> DataTablesResponse<T,M> newInstance(DataTablesRequest request, Integer rowCount) {
		Integer rCount = NumberUtilities.getNullSafe(rowCount);
		DataTablesResponse<T,M> response = new DataTablesResponse<T,M>();
		response.setEcho(request.getEcho());
		response.setTotalRecords(rCount);
		response.setTotalDisplayRecords(rCount);
		return response;
	}
}

package com.workmarket.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

public class DataTablesWithHeaderResponse<T,M> extends DataTablesResponse<T,M> {

	@JsonProperty(value = "aoColumnDefs")
	private List<DataTableColumnHeader> columns = Lists.newArrayList();

	public static <T,M> DataTablesWithHeaderResponse<T,M> newInstance(DataTablesRequest request) {
		DataTablesWithHeaderResponse<T,M> response = new DataTablesWithHeaderResponse<T,M>();
		response.setEcho(request.getEcho());
		return response;
	}

	public List<DataTableColumnHeader> getColumns() {
		return columns;
	}

	public void setColumns(List<DataTableColumnHeader> columns) {
		this.columns = columns;
	}

	public void addColumn(DataTableColumnHeader column) {
		columns.add(column);
	}
}

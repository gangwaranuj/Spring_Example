package com.workmarket.domains.model.clientservice;

import java.util.Map;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ClientServiceAlertPagination extends AbstractPagination<ClientServiceAlert> implements Pagination<ClientServiceAlert> {
	
	public ClientServiceAlertPagination() {}

	public ClientServiceAlertPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		COMPANY_ID("company.id");

		private String column;

		FILTER_KEYS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	private Map<String, String> columns = Maps.newHashMap();
	private Map<FILTER_KEYS, Object> resultFilters = Maps.newHashMap();

	public Map<String, String> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, String> columns) {
		this.columns = columns;
	}

	public Map<FILTER_KEYS, Object> getResultFilters() {
		return resultFilters;
	}

	public void setResultFilters(Map<FILTER_KEYS, Object> resultFilters) {
		this.resultFilters = resultFilters;
	}

	public enum SORTS {
			COMPANY_ID, ALERT_DATE
	}
}

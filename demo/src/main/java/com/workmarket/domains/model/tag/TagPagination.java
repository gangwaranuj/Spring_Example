package com.workmarket.domains.model.tag;

import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class TagPagination extends AbstractPagination<Tag> implements Pagination<Tag> {

	private Map<String, String> columns = Maps.newHashMap();
	private Multimap<FILTER_KEYS, Object> resultFilters = ArrayListMultimap.create();
	private Map<SORTS, String> resultSorts = Maps.newHashMap();

	public Map<String, String> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, String> columns) {
		this.columns = columns;
	}

	public Multimap<FILTER_KEYS, Object> getResultFilters() {
		return resultFilters;
	}

	public void setResultFilters(Multimap<FILTER_KEYS, Object> resultFilters) {
		this.resultFilters = resultFilters;
	}

	public Map<SORTS, String> getResultSorts() {
		return resultSorts;
	}

	public enum FILTER_KEYS {
		APPROVAL_STATUS("approvalStatus");
		private String column;

		FILTER_KEYS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public enum SORTS {
		NAME("name"),
		CREATED_ON("createdOn");
		private String column;
		private String direction;

		SORTS(String column) {
			this.column = column;
//            this.direction = direction;
		}

		public String getColumn() {
			return column;
		}

		public String getDirection() {
			return direction;
		}
	}
}

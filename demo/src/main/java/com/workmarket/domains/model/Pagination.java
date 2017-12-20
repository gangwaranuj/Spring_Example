package com.workmarket.domains.model;

import java.util.List;
import java.util.Map;

public interface Pagination<T> {

	int MAX_ROWS = 500;
	
	enum SORT_DIRECTION {ASC, DESC}
	
	void setResultsLimit(Integer numResults);
	Integer getResultsLimit();
	
	void setStartRow(Integer startRow);
	Integer getStartRow();
	
	void setRowCount(Integer rowCount);
	Integer getRowCount(); 
	
	void setSortColumn(String sortColumn);
	void setSortColumn(Enum<?> sortColumn);

	boolean hasSortColumn();
	String getSortColumn();
	String getSortColumn(String defaultSortColumn);
	String getSortColumn(Enum<?> defaultSortColumn);
	
	List<T> getResults();
	
	void setResults(List<T> results);

	boolean hasFilter(Enum<?> key);
	boolean hasFilters();
	String getFilter(Enum<?> key);
	Map<String, String> getFilters();
	void setFilters(Map<String, String> filters);

	void addFilter(String key, String value);
	void addFilter(Enum<?> key, String value);
	void addFilter(Enum<?> key, Object value);
	
	SORT_DIRECTION getSortDirection();
	SORT_DIRECTION getSortDirection(SORT_DIRECTION defaultSortDirection);
	void setSortDirection(SORT_DIRECTION sortDirection);

	void setReturnAllRows();
	void setReturnAllRows(boolean limitMaxRows);
}

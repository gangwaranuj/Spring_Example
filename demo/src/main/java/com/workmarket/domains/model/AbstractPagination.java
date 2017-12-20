package com.workmarket.domains.model;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractPagination<T> implements Pagination<T> {

	private Integer resultsLimit = Pagination.MAX_ROWS;
	private Integer startRow = 0;
	private Integer rowCount;
	private String sortColumn;
	private List<T> results = Lists.newArrayList();
	private Map<String, String> filters = Maps.newHashMap();
	private SORT_DIRECTION sortDirection = Pagination.SORT_DIRECTION.ASC;
	public String[] projection = new String[0];
	private Map[] projectionResults = new Map[0];
	private List<Sort> sorts = Lists.newArrayList();

	private boolean limitMaxRows = true;

	protected AbstractPagination() {}
	protected AbstractPagination(boolean returnAllRows) {
		if (returnAllRows) {
			setReturnAllRows();
		}
	}

	/**
	 * Sets the size of the page
	 *
	 * @param resultsLimit -
	 */
	@Override
	public void setResultsLimit(Integer resultsLimit) {
		this.resultsLimit = resultsLimit;
	}

	@Override
	public Integer getResultsLimit() {
		if (resultsLimit == null) {
			setReturnAllRows(isLimitMaxRows());
		}
		return resultsLimit;
	}

	/**
	 * Sets the first row to be fetched
	 *
	 * @param startRow -
	 */
	@Override
	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	@Override
	public Integer getStartRow() {
		return startRow;
	}

	@Override
	public void setRowCount(Integer rowCount) {
		this.rowCount = (rowCount != null) ? rowCount : 0;
	}

	public void setRowCount(Long rowCount) {
		this.rowCount = (rowCount != null) ? rowCount.intValue() : 0;
	}

	@Override
	public Integer getRowCount() {
		return rowCount;
	}

	@Override
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	@Override
	public void setSortColumn(Enum<?> sortColumn) {
		this.sortColumn = sortColumn.toString();
	}

	@Override
	public String getSortColumn() {
		return sortColumn;
	}

	@Override
	public boolean hasSortColumn() {
		return StringUtils.isNotBlank(sortColumn);
	}

	@Override
	public String getSortColumn(String defaultSortColumn) {
		return MoreObjects.firstNonNull(sortColumn, defaultSortColumn);
	}

	@Override
	public String getSortColumn(Enum<?> defaultSortColumn) {
		return MoreObjects.firstNonNull(sortColumn, defaultSortColumn.toString());
	}

	@Override
	public void setResults(List<T> results) {
		if (results.size() > 0)
			this.results = results;
		else
			this.results = Lists.newArrayList();
	}

	@Override
	public List<T> getResults() {
		if (results == null) {
			return Collections.emptyList();
		}
		return results;
	}

	@Override
	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}

	@Override
	public Map<String, String> getFilters() {
		if (filters == null) {
			filters = Maps.newHashMap();
		}
		return filters;
	}

	@Override
	public void addFilter(String key, String value) {
		filters.put(key, value);
	}

	@Override
	public void addFilter(Enum<?> key, String value) {
		addFilter(key.toString(), value);
	}

	@Override
	public void addFilter(Enum<?> key, Object value) {
		addFilter(key.toString(), value.toString());
	}

	@Override
	public String getFilter(Enum<?> key) {
		if (filters.containsKey(key))
			return filters.get(key);
		if (filters.containsKey(key.toString()))
			return filters.get(key.toString());
		return null;
	}

	@Override
	public boolean hasFilter(Enum<?> key) {
		if (filters.containsKey(key))
			return true;
		if (filters.containsKey(key.toString()))
			return true;
		return false;
	}

	public boolean hasFilters() {
		return (filters != null && !filters.isEmpty());
	}

	@Override
	public SORT_DIRECTION getSortDirection() {
		return sortDirection;
	}

	@Override
	public SORT_DIRECTION getSortDirection(SORT_DIRECTION defaultSortDirection) {
		return MoreObjects.firstNonNull(sortDirection, defaultSortDirection);
	}

	@Override
	public void setSortDirection(SORT_DIRECTION sortDirection) {
		this.sortDirection = sortDirection;
	}

	@Override
	public void setReturnAllRows() {
		setReturnAllRows(true);
	}

	@Override
	public void setReturnAllRows(boolean returnAllRows) {
		setLimitMaxRows(!returnAllRows);
		setResultsLimit(isLimitMaxRows() ? Pagination.MAX_ROWS : Integer.MAX_VALUE);
		setStartRow(0);
	}

	public Integer getNumberOfPages() {
		return getRowCount() / getResultsLimit() + (getRowCount() % getResultsLimit() > 0 ? 1 : 0);
	}

	public Integer getCurrentPage() {
		return getStartRow() / getResultsLimit() + (getStartRow() % getResultsLimit() >= 0 ? 1 : 0);
	}

	public boolean hasNextPage() {
		return getCurrentPage() < getNumberOfPages();
	}

	public void setPage(int n) {
		setStartRow(getResultsLimit() * ((n <= 1) ? 0 : n - 1));
	}

	public void nextPage() {
		setStartRow(getStartRow() + getResultsLimit());
	}

	public String[] getProjection() {
		return projection;
	}

	public void setProjection(String[] projection) {
		this.projection = projection;
	}

	public Map[] getProjectionResults() {
		return projectionResults;
	}

	public void setProjectionResults(Map[] projectionResults) {
		this.projectionResults = projectionResults;
	}

	public boolean isLastPage() {
		return getStartRow() + getResultsLimit() >= getRowCount();
	}

	public boolean isLimitMaxRows() {
		return limitMaxRows;
	}

	private void setLimitMaxRows(boolean limitMaxRows) {
		this.limitMaxRows = limitMaxRows;
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
	}

	public void addSort(Sort sort) {
		getSorts().add(sort);
	}

	public void addSort(String sortColumn, Pagination.SORT_DIRECTION sortDirection) {
		Sort sort = new Sort(sortColumn, sortDirection);
		addSort(sort);
	}


}

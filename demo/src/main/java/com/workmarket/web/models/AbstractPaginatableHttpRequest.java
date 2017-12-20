package com.workmarket.web.models;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.Pagination;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Map;

public abstract class AbstractPaginatableHttpRequest implements PaginatableHttpRequest {
	Integer start;
	Integer limit;
	Integer sortColumnIndex;
	String sortColumnDirection;
	Map<Integer,String> sortableColumnMapping = Maps.newHashMap();
	Map<String,Enum<?>> filterMapping = Maps.newHashMap();
	Map<String,String[]> requestParams = Maps.newHashMap();
	Object backingBean;

	@Override
	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	@Override
	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@Override
	public Integer getSortColumnIndex() {
		return sortColumnIndex;
	}

	public void setSortColumnIndex(Integer sortColumnIndex) {
		this.sortColumnIndex = sortColumnIndex;
	}

	@Override
	public String getSortColumn() {
		return sortableColumnMapping.get(sortColumnIndex);
	}

	public void setSortColumnDirection(String sortColumnDirection) {
		this.sortColumnDirection = sortColumnDirection;
	}

	@Override
	public Pagination.SORT_DIRECTION getSortColumnDirection() {
		try {
			return Pagination.SORT_DIRECTION.valueOf(StringUtils.upperCase(sortColumnDirection));
		} catch (Exception e) {
			return Pagination.SORT_DIRECTION.ASC;
		}
	}

	public Map<Integer,String> getSortableColumnMapping() {
		return sortableColumnMapping;
	}

	public void setSortableColumnMapping(Map<Integer,String> sortableColumnMapping) {
		this.sortableColumnMapping = sortableColumnMapping;
	}

	public Map<String,Enum<?>> getFilterMapping() {
		return filterMapping;
	}

	public void setFilterMapping(Map<String,Enum<?>> filterMapping) {
		this.filterMapping = filterMapping;
	}

	public Map<String,String[]> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(Map<String,String[]> requestParams) {
		this.requestParams = requestParams;
	}

	public Object getBackingBean() {
		return backingBean;
	}

	public void setBackingBean(Object backingBean) {
		this.backingBean = backingBean;
	}

	public <T extends Pagination> T newPagination(Class<T> clazz) throws InstantiationException, IllegalAccessException {
		return newPagination(clazz, false);
	}
	
	public <T extends Pagination> T newPagination(Class<T> clazz, boolean returnAllResults) throws InstantiationException, IllegalAccessException {
		T pagination = clazz.newInstance();
		if (returnAllResults) {
			pagination.setReturnAllRows();
		} else {
			pagination.setStartRow(getStart());
			pagination.setResultsLimit(getLimit());
		}
		pagination.setSortColumn(getSortColumn());
		pagination.setSortDirection(getSortColumnDirection());

		initializeFilterMapping(pagination);

		return pagination;
	}

	private void initializeFilterMapping(Pagination pagination) {
		for (String paramKey : filterMapping.keySet()) {
			String value = null;
			if (backingBean != null) {
				// In most cases, pagination filters (which are always strings... for now)
				// expect date filters to be provided in ISO-8061 format. If we have a backing
				// bean and know the type, let's do that conversion here.
				// TODO Probably a better way...
				try {
					Object o = PropertyUtils.getProperty(backingBean, paramKey);
					if (o instanceof Calendar) {
						value = DateUtilities.getISO8601((Calendar)o);
					} else {
						value = o.toString();
					}
				} catch (Exception e) {
					continue;
				}
			} else {
				if (!getRequestParams().containsKey(paramKey)) continue;
				String[] values = getRequestParams().get(paramKey);
				if (values.length == 0) continue;
				value = values[0];
			}
			if (StringUtils.isBlank(value)) continue;
			pagination.addFilter(filterMapping.get(paramKey), value);
		}
	}
}
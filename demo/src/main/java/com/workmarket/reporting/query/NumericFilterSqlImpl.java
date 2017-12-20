package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.Filter;
import com.workmarket.domains.model.reporting.NumericFilter;

public class NumericFilterSqlImpl extends AbstractFilter {

	public <T extends Filter> void filter(T filter) throws Exception {

		NumericFilter numericFilter = (NumericFilter) filter;
		filter(filter, numericFilter.getFromValue(), numericFilter.getToValue());

	}
}

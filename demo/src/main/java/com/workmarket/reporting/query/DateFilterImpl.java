package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.DateFilter;
import com.workmarket.domains.model.reporting.Filter;

public class DateFilterImpl extends AbstractFilter {

	/*
	 * Instance variables and constants
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.workmarket.reporting.query.AbstractFilter#filter(com.workmarket.reporting.model.Filter)
	 */
	public <T extends Filter> void filter(T filter) throws Exception {
		DateFilter dateFilter = (DateFilter) filter;
		filter(filter, dateFilter.getFromDateC(), dateFilter.getToDateC());
	}
}

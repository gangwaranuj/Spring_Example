package com.workmarket.reporting.query;


import com.workmarket.domains.model.reporting.FieldValueFilter;
import com.workmarket.domains.model.reporting.Filter;

import org.hibernate.criterion.Restrictions;

import com.workmarket.reporting.util.ReportingUtil;


public class FieldValueFilterImpl extends AbstractFilter {

	/* (non-Javadoc)
	 * @see com.workmarket.reporting.query.AbstractFilter#filter(com.workmarket.reporting.model.Filter)
	 */
	public <T extends Filter> void filter(T filter) throws Exception {

		FieldValueFilter fieldValueFilter = (FieldValueFilter)filter;
		String property = ReportingUtil.getRootProperty(fieldValueFilter.getProperty());
		
		switch(fieldValueFilter.getEqualNotEqualTo()){
			case EQUAL_TO:
				fieldValueFilter.getCriteria().add(Restrictions.eq(property, fieldValueFilter.getFieldValue()));
				//fieldValueFilter.getCriteria().add(Restrictions.sqlRestriction("title = 'Website Development - Joomla Site'"));
				break;
			case NOT_EQUAL_TO:
				fieldValueFilter.getCriteria().add(Restrictions.ne(property, fieldValueFilter.getFieldValue()));
				break;
		}
	}

}

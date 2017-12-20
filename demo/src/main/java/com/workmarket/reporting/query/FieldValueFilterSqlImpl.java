package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.FieldValueFilter;
import com.workmarket.domains.model.reporting.Filter;
import org.apache.commons.lang.StringUtils;

public class FieldValueFilterSqlImpl extends AbstractFilter {

	public <T extends Filter> void filter(T filter) throws Exception {

		FieldValueFilter fieldValueFilter = (FieldValueFilter) filter;
		if(StringUtils.isEmpty(fieldValueFilter.getFieldValue())){
			return;
		}
		String[] fieldValues = fieldValueFilter.getFieldValue().split(",");
		StringBuffer inClause = new StringBuffer("");
		for(int i = 0;i<fieldValues.length;i++){
			inClause.append("'" + fieldValues[i] + "'");
			if((i + 1) != fieldValues.length){
				inClause.append(",");
			}
		}

		switch (fieldValueFilter.getEqualNotEqualTo()) {
			case EQUAL_TO:{
				String whereClause = fieldValueFilter.getDbTableAndField() + " IN (" + inClause.toString() + ")";
				fieldValueFilter.getSqlBuilder().addWhereClause(whereClause);
				break;
			}
			case NOT_EQUAL_TO:{
				String whereClause = fieldValueFilter.getDbTableAndField() + " NOT IN (" + inClause.toString() + ")";
				fieldValueFilter.getSqlBuilder().addWhereClause(whereClause);
				break;
			}
		}
	}

}

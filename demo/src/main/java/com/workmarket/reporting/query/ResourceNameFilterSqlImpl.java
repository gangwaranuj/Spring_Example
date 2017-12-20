package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.FieldValueFilter;
import com.workmarket.domains.model.reporting.Filter;
import org.apache.commons.lang.StringUtils;

public class ResourceNameFilterSqlImpl extends AbstractFilter {

	public <T extends Filter> void filter(T filter) throws Exception {
		FieldValueFilter fieldValueFilter = (FieldValueFilter) filter;

		String[] namesList = fieldValueFilter.getFieldValue().split(",");
		StringBuffer inClause = new StringBuffer("");
		for(int i = 0;i<namesList.length;i++){
			String name = StringUtils.trim(namesList[i]);
			if(StringUtils.isEmpty(name)) continue;
			if(i>0) inClause.append(",");
			inClause.append("'" + name + "'");
		}
		String whereClause = "CONCAT(user.first_name,' ',user.last_name) IN (" + inClause.toString() + ")";
		fieldValueFilter.getSqlBuilder().addWhereClause(whereClause.toString());
	}
}

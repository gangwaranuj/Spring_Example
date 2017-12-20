package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.Entity;

public class ResourceNameCustomSql implements CustomSql{

	public String getSelectSql(String resource, Entity entity) {
		return "";
	}

	public String getCriteriaSql(String resource, Entity entity) {
		return "";
	}

	public AbstractFilter getFilter(String resource) {
		return new ResourceNameFilterSqlImpl();
	}

}

package com.workmarket.domains.model.reporting;

public class DynamicFilterInputTag extends FilterInputTag{
	private static final long serialVersionUID = -8563829439552826319L;
	
	private String selectOptionsSql;
	
	public String getSelectOptionsSql() {
		return selectOptionsSql;
	}

	public void setSelectOptionsSql(String selectOptionsSql) {
		this.selectOptionsSql = selectOptionsSql;
	}
}

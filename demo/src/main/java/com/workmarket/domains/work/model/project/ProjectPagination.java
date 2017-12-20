package com.workmarket.domains.work.model.project;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.utility.sql.SQLOperator;

public class ProjectPagination extends AbstractPagination<Project> implements Pagination<Project> {
	
	public enum FILTER_KEYS {
		ACTIVE("active", SQLOperator.EQUALS),
		RESERVED_FUNDS_ENABLED("reservedFundsEnabled", SQLOperator.EQUALS);
		
		private String column;
		private String operator; 
		
		FILTER_KEYS(String column, String operator) {
			this.column = column;
			this.operator = operator;
		}

		public String getColumn() {
			return column;
		}
		
		public String getOperator() {
			return operator;
		}
		
		public String getOperationWithParam(String paramName) {
			return getColumn() + getOperator() + " " + paramName;
		}
	}
	
	public enum SORTS {
		NAME("name"),
		DESCRIPTION("description"),
		DUE_DATE("dueDate"),
		OWNER("owner.lastName"),
		CLIENT("clientCompany.name");

		private String columnName;

		SORTS(String columnName) {
			this.columnName = columnName;
		}

		public String getColumnName() {
			return columnName;
		}
	}
	
	public ProjectPagination() {
		
	}
	
	public ProjectPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}

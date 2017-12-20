package com.workmarket.domains.model.tool;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ToolPagination extends AbstractPagination<Tool> implements Pagination<Tool> {

    public ToolPagination() {
    }

    public ToolPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS {
		NAME("name"),
		POPULARITY("popularity");

		private String columnName;

		FILTER_KEYS(String columnName) {
			this.columnName = columnName;
		}

		public String getColumnName() {
			return columnName;
		}
	}

    public enum SORTS {
        POPULARITY("unused"),
        NAME("name"),
		INDUSTRY_NAME("industry.name"),
		CREATED_ON("createdOn"),
		CREATOR_LAST_NAME("creator.lastName");

        private String columnName;

        SORTS(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }
}

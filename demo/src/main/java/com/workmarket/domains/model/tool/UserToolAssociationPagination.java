package com.workmarket.domains.model.tool;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserToolAssociationPagination extends AbstractPagination<UserToolAssociation> implements Pagination<UserToolAssociation>
{

	public UserToolAssociationPagination()
	{
	}

	public UserToolAssociationPagination(boolean returnAllRows)
	{
		super(returnAllRows);
	}

	public enum FILTER_KEYS
	{
	}

	public enum SORTS
	{
		TOOL_NAME("tool.name");

		private String columnName;

		SORTS(String columnName)
		{
			this.columnName = columnName;
		}

		public String getColumnName()
		{
			return columnName;
		}
	}
}

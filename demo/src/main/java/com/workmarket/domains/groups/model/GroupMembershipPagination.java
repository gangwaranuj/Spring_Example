package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.service.business.dto.GroupMembershipDTO;


public class GroupMembershipPagination extends AbstractPagination<GroupMembershipDTO> implements Pagination<GroupMembershipDTO> {

	public enum FILTER_KEYS {}

	public enum SORTS {
		USER_LAST_NAME("user.last_name"),
		USER_FIRST_NAME("user.first_name"),
		STATUS("status");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public GroupMembershipPagination() {
		setSortColumn(SORTS.USER_LAST_NAME.toString());
	}

	public GroupMembershipPagination(boolean returnAllRows) {
		super(returnAllRows);
		setSortColumn(SORTS.USER_LAST_NAME.toString());
	}
}

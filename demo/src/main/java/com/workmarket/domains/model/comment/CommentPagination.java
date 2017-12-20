package com.workmarket.domains.model.comment;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class CommentPagination extends AbstractPagination<Comment> implements Pagination<Comment> {

	public CommentPagination() {}
	public CommentPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		USER_ID("user.id");

		private String column;

		FILTER_KEYS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public enum SORTS {
		CREATED_ON("id");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

}

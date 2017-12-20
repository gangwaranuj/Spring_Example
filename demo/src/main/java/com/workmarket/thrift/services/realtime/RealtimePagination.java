package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RealtimePagination implements Serializable {
	private static final long serialVersionUID = 1L;

	private int pageSize;
	private int cursorPosition;
	private SortDirectionType sortDirection;
	private SortByType sortBy;
	private int maximumNumberOfResourcesPerAssignment;

	public RealtimePagination() {
		this.pageSize = 25;
		this.cursorPosition = 0;
		this.maximumNumberOfResourcesPerAssignment = 500;
	}

	public RealtimePagination(int pageSize, int cursorPosition, int maximumNumberOfResourcesPerAssignment) {
		this();
		this.pageSize = pageSize;
		this.cursorPosition = cursorPosition;
		this.maximumNumberOfResourcesPerAssignment = maximumNumberOfResourcesPerAssignment;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public RealtimePagination setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public boolean isSetPageSize() {
		return (pageSize > 0);
	}

	public int getCursorPosition() {
		return this.cursorPosition;
	}

	public RealtimePagination setCursorPosition(int cursorPosition) {
		this.cursorPosition = cursorPosition;
		return this;
	}

	public boolean isSetCursorPosition() {
		return (cursorPosition > 0);
	}

	public SortDirectionType getSortDirection() {
		return this.sortDirection;
	}

	public RealtimePagination setSortDirection(SortDirectionType sortDirection) {
		this.sortDirection = sortDirection;
		return this;
	}

	public boolean isSetSortDirection() {
		return this.sortDirection != null;
	}

	public SortByType getSortBy() {
		return this.sortBy;
	}

	public RealtimePagination setSortBy(SortByType sortBy) {
		this.sortBy = sortBy;
		return this;
	}

	public boolean isSetSortBy() {
		return this.sortBy != null;
	}

	public int getMaximumNumberOfResourcesPerAssignment() {
		return this.maximumNumberOfResourcesPerAssignment;
	}

	public RealtimePagination setMaximumNumberOfResourcesPerAssignment(int maximumNumberOfResourcesPerAssignment) {
		this.maximumNumberOfResourcesPerAssignment = maximumNumberOfResourcesPerAssignment;
		return this;
	}

	public boolean isSetMaximumNumberOfResourcesPerAssignment() {
		return (maximumNumberOfResourcesPerAssignment > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimePagination)
			return this.equals((RealtimePagination) that);
		return false;
	}

	private boolean equals(RealtimePagination that) {
		if (that == null)
			return false;

		boolean this_present_pageSize = true;
		boolean that_present_pageSize = true;
		if (this_present_pageSize || that_present_pageSize) {
			if (!(this_present_pageSize && that_present_pageSize))
				return false;
			if (this.pageSize != that.pageSize)
				return false;
		}

		boolean this_present_cursorPosition = true;
		boolean that_present_cursorPosition = true;
		if (this_present_cursorPosition || that_present_cursorPosition) {
			if (!(this_present_cursorPosition && that_present_cursorPosition))
				return false;
			if (this.cursorPosition != that.cursorPosition)
				return false;
		}

		boolean this_present_sortDirection = true && this.isSetSortDirection();
		boolean that_present_sortDirection = true && that.isSetSortDirection();
		if (this_present_sortDirection || that_present_sortDirection) {
			if (!(this_present_sortDirection && that_present_sortDirection))
				return false;
			if (!this.sortDirection.equals(that.sortDirection))
				return false;
		}

		boolean this_present_sortBy = true && this.isSetSortBy();
		boolean that_present_sortBy = true && that.isSetSortBy();
		if (this_present_sortBy || that_present_sortBy) {
			if (!(this_present_sortBy && that_present_sortBy))
				return false;
			if (!this.sortBy.equals(that.sortBy))
				return false;
		}

		boolean this_present_maximumNumberOfResourcesPerAssignment = true;
		boolean that_present_maximumNumberOfResourcesPerAssignment = true;
		if (this_present_maximumNumberOfResourcesPerAssignment || that_present_maximumNumberOfResourcesPerAssignment) {
			if (!(this_present_maximumNumberOfResourcesPerAssignment && that_present_maximumNumberOfResourcesPerAssignment))
				return false;
			if (this.maximumNumberOfResourcesPerAssignment != that.maximumNumberOfResourcesPerAssignment)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_pageSize = true;
		builder.append(present_pageSize);
		if (present_pageSize)
			builder.append(pageSize);

		boolean present_cursorPosition = true;
		builder.append(present_cursorPosition);
		if (present_cursorPosition)
			builder.append(cursorPosition);

		boolean present_sortDirection = true && (isSetSortDirection());
		builder.append(present_sortDirection);
		if (present_sortDirection)
			builder.append(sortDirection.getValue());

		boolean present_sortBy = true && (isSetSortBy());
		builder.append(present_sortBy);
		if (present_sortBy)
			builder.append(sortBy.getValue());

		boolean present_maximumNumberOfResourcesPerAssignment = true;
		builder.append(present_maximumNumberOfResourcesPerAssignment);
		if (present_maximumNumberOfResourcesPerAssignment)
			builder.append(maximumNumberOfResourcesPerAssignment);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimePagination(");
		boolean first = true;

		sb.append("pageSize:");
		sb.append(this.pageSize);
		first = false;
		if (!first) sb.append(", ");
		sb.append("cursorPosition:");
		sb.append(this.cursorPosition);
		first = false;
		if (isSetSortDirection()) {
			if (!first) sb.append(", ");
			sb.append("sortDirection:");
			if (this.sortDirection == null) {
				sb.append("null");
			} else {
				sb.append(this.sortDirection);
			}
			first = false;
		}
		if (isSetSortBy()) {
			if (!first) sb.append(", ");
			sb.append("sortBy:");
			if (this.sortBy == null) {
				sb.append("null");
			} else {
				sb.append(this.sortBy);
			}
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("maximumNumberOfResourcesPerAssignment:");
		sb.append(this.maximumNumberOfResourcesPerAssignment);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
package com.workmarket.search.request.user;

import com.workmarket.search.SortDirectionType;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Pagination implements Serializable {
	private static final long serialVersionUID = 1L;

	private PeopleSearchSortByType sortBy;
	private SortDirectionType sortDirection;
	private long cursorPosition;
	private int pageNumber;
	private int pageSize;

	public Pagination() {
		this.sortBy = PeopleSearchSortByType.RELEVANCY;
		this.sortDirection = SortDirectionType.DESC;
	}

	public Pagination(
			PeopleSearchSortByType sortBy,
			SortDirectionType sortDirection,
			long cursorPosition,
			int pageNumber,
			int pageSize) {
		this();
		this.sortBy = sortBy;
		this.sortDirection = sortDirection;
		this.cursorPosition = cursorPosition;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public PeopleSearchSortByType getSortBy() {
		return this.sortBy;
	}

	public Pagination setSortBy(PeopleSearchSortByType sortBy) {
		this.sortBy = sortBy;
		return this;
	}

	public boolean isSetSortBy() {
		return this.sortBy != null;
	}

	public SortDirectionType getSortDirection() {
		return this.sortDirection;
	}

	public Pagination setSortDirection(SortDirectionType sortDirection) {
		this.sortDirection = sortDirection;
		return this;
	}

	public boolean isSetSortDirection() {
		return this.sortDirection != null;
	}

	public long getCursorPosition() {
		return this.cursorPosition;
	}

	public Pagination setCursorPosition(long cursorPosition) {
		this.cursorPosition = cursorPosition;
		return this;
	}

	public boolean isSetCursorPosition() {
		return (cursorPosition > 0L);
	}

	public int getPageNumber() {
		return this.pageNumber;
	}

	public Pagination setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		return this;
	}

	public boolean isSetPageNumber() {
		return (pageNumber > 0);
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public Pagination setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public boolean isSetPageSize() {
		return (pageSize > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Pagination)
			return this.equals((Pagination) that);
		return false;
	}

	private boolean equals(Pagination that) {
		if (that == null)
			return false;

		boolean this_present_sortBy = true && this.isSetSortBy();
		boolean that_present_sortBy = true && that.isSetSortBy();
		if (this_present_sortBy || that_present_sortBy) {
			if (!(this_present_sortBy && that_present_sortBy))
				return false;
			if (!this.sortBy.equals(that.sortBy))
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

		boolean this_present_cursorPosition = true;
		boolean that_present_cursorPosition = true;
		if (this_present_cursorPosition || that_present_cursorPosition) {
			if (!(this_present_cursorPosition && that_present_cursorPosition))
				return false;
			if (this.cursorPosition != that.cursorPosition)
				return false;
		}

		boolean this_present_pageNumber = true;
		boolean that_present_pageNumber = true;
		if (this_present_pageNumber || that_present_pageNumber) {
			if (!(this_present_pageNumber && that_present_pageNumber))
				return false;
			if (this.pageNumber != that.pageNumber)
				return false;
		}

		boolean this_present_pageSize = true;
		boolean that_present_pageSize = true;
		if (this_present_pageSize || that_present_pageSize) {
			if (!(this_present_pageSize && that_present_pageSize))
				return false;
			if (this.pageSize != that.pageSize)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_sortBy = true && (isSetSortBy());
		builder.append(present_sortBy);

		boolean present_sortDirection = true && (isSetSortDirection());
		builder.append(present_sortDirection);
		if (present_sortDirection)
			builder.append(sortDirection.getValue());

		boolean present_cursorPosition = true;
		builder.append(present_cursorPosition);
		if (present_cursorPosition)
			builder.append(cursorPosition);

		boolean present_pageNumber = true;
		builder.append(present_pageNumber);
		if (present_pageNumber)
			builder.append(pageNumber);

		boolean present_pageSize = true;
		builder.append(present_pageSize);
		if (present_pageSize)
			builder.append(pageSize);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Pagination(");
		boolean first = true;

		sb.append("sortBy:");
		if (this.sortBy == null) {
			sb.append("null");
		} else {
			sb.append(this.sortBy);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("sortDirection:");
		if (this.sortDirection == null) {
			sb.append("null");
		} else {
			sb.append(this.sortDirection);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("cursorPosition:");
		sb.append(this.cursorPosition);
		first = false;
		if (!first) sb.append(", ");
		sb.append("pageNumber:");
		sb.append(this.pageNumber);
		first = false;
		if (!first) sb.append(", ");
		sb.append("pageSize:");
		sb.append(this.pageSize);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


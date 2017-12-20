package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class PaginationPageThrift implements Serializable {
	private static final long serialVersionUID = 1L;

	private int startRow;
	private int pageSize;

	public PaginationPageThrift() {
	}

	public PaginationPageThrift(int startRow, int pageSize) {
		this();
		this.startRow = startRow;
		this.pageSize = pageSize;
	}

	public int getStartRow() {
		return this.startRow;
	}

	public PaginationPageThrift setStartRow(int startRow) {
		this.startRow = startRow;
		return this;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public PaginationPageThrift setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof PaginationPageThrift)
			return this.equals((PaginationPageThrift) that);
		return false;
	}

	private boolean equals(PaginationPageThrift that) {
		if (that == null)
			return false;

		boolean this_present_startRow = true;
		boolean that_present_startRow = true;
		if (this_present_startRow || that_present_startRow) {
			if (!(this_present_startRow && that_present_startRow))
				return false;
			if (this.startRow != that.startRow)
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

		boolean present_startRow = true;
		builder.append(present_startRow);
		if (present_startRow)
			builder.append(startRow);

		boolean present_pageSize = true;
		builder.append(present_pageSize);
		if (present_pageSize)
			builder.append(pageSize);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PaginationPageThrift(");
		boolean first = true;

		sb.append("startRow:");
		sb.append(this.startRow);
		first = false;
		if (!first) sb.append(", ");
		sb.append("pageSize:");
		sb.append(this.pageSize);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
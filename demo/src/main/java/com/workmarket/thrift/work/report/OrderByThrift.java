package com.workmarket.thrift.work.report;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class OrderByThrift implements Serializable {
	private static final long serialVersionUID = 1L;

	private WorkReportColumnType column;
	private boolean desc;

	public OrderByThrift() {
	}

	public OrderByThrift(WorkReportColumnType column, boolean desc) {
		this();
		this.column = column;
		this.desc = desc;
	}

	public WorkReportColumnType getColumn() {
		return this.column;
	}

	public OrderByThrift setColumn(WorkReportColumnType column) {
		this.column = column;
		return this;
	}

	public boolean isSetColumn() {
		return this.column != null;
	}

	public boolean isDesc() {
		return this.desc;
	}

	public OrderByThrift setDesc(boolean desc) {
		this.desc = desc;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof OrderByThrift)
			return this.equals((OrderByThrift) that);
		return false;
	}

	private boolean equals(OrderByThrift that) {
		if (that == null)
			return false;

		boolean this_present_column = true && this.isSetColumn();
		boolean that_present_column = true && that.isSetColumn();
		if (this_present_column || that_present_column) {
			if (!(this_present_column && that_present_column))
				return false;
			if (!this.column.equals(that.column))
				return false;
		}

		boolean this_present_desc = true;
		boolean that_present_desc = true;
		if (this_present_desc || that_present_desc) {
			if (!(this_present_desc && that_present_desc))
				return false;
			if (this.desc != that.desc)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_column = true && (isSetColumn());
		builder.append(present_column);
		if (present_column)
			builder.append(column.getValue());

		boolean present_desc = true;
		builder.append(present_desc);
		if (present_desc)
			builder.append(desc);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("OrderByThrift(");
		boolean first = true;

		sb.append("column:");
		if (this.column == null) {
			sb.append("null");
		} else {
			sb.append(this.column);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("desc:");
		sb.append(this.desc);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
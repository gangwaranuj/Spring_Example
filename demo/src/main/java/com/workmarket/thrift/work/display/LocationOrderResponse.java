package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class LocationOrderResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private int row;
	private int index;
	private int column;

	public LocationOrderResponse() {
	}

	public LocationOrderResponse(int row, int index, int column) {
		this();
		this.row = row;
		this.index = index;
		this.column = column;
	}

	public int getRow() {
		return this.row;
	}

	public LocationOrderResponse setRow(int row) {
		this.row = row;
		return this;
	}

	public boolean isSetRow() {
		return (row > 0);
	}

	public int getIndex() {
		return this.index;
	}

	public LocationOrderResponse setIndex(int index) {
		this.index = index;
		return this;
	}

	public boolean isSetIndex() {
		return (index > 0);
	}

	public int getColumn() {
		return this.column;
	}

	public LocationOrderResponse setColumn(int column) {
		this.column = column;
		return this;
	}

	public boolean isSetColumn() {
		return (column > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof LocationOrderResponse)
			return this.equals((LocationOrderResponse) that);
		return false;
	}

	private boolean equals(LocationOrderResponse that) {
		if (that == null)
			return false;

		boolean this_present_row = true;
		boolean that_present_row = true;
		if (this_present_row || that_present_row) {
			if (!(this_present_row && that_present_row))
				return false;
			if (this.row != that.row)
				return false;
		}

		boolean this_present_index = true;
		boolean that_present_index = true;
		if (this_present_index || that_present_index) {
			if (!(this_present_index && that_present_index))
				return false;
			if (this.index != that.index)
				return false;
		}

		boolean this_present_column = true;
		boolean that_present_column = true;
		if (this_present_column || that_present_column) {
			if (!(this_present_column && that_present_column))
				return false;
			if (this.column != that.column)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_row = true;
		builder.append(present_row);
		if (present_row)
			builder.append(row);

		boolean present_index = true;
		builder.append(present_index);
		if (present_index)
			builder.append(index);

		boolean present_column = true;
		builder.append(present_column);
		if (present_column)
			builder.append(column);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("LocationOrderResponse(");
		boolean first = true;

		sb.append("row:");
		sb.append(this.row);
		first = false;
		if (!first) sb.append(", ");
		sb.append("index:");
		sb.append(this.index);
		first = false;
		if (!first) sb.append(", ");
		sb.append("column:");
		sb.append(this.column);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
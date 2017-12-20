package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class FieldMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	private FieldType type;
	private String columnName;
	private int columnIndex;
	private String sampleValue;

	public FieldMapping() {
	}

	public FieldMapping(FieldType type, int columnIndex) {
		this();
		this.type = type;
		this.columnIndex = columnIndex;
	}

	public FieldType getType() {
		return this.type;
	}

	public FieldMapping setType(FieldType type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public FieldMapping setColumnName(String columnName) {
		this.columnName = columnName;
		return this;
	}

	public boolean isSetColumnName() {
		return this.columnName != null;
	}

	public int getColumnIndex() {
		return this.columnIndex;
	}

	public FieldMapping setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
		return this;
	}

	public boolean isSetColumnIndex() {
		return (columnIndex > 0);
	}

	public String getSampleValue() {
		return this.sampleValue;
	}

	public FieldMapping setSampleValue(String sampleValue) {
		this.sampleValue = sampleValue;
		return this;
	}

	public boolean isSetSampleValue() {
		return this.sampleValue != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FieldMapping)
			return this.equals((FieldMapping) that);
		return false;
	}

	private boolean equals(FieldMapping that) {
		if (that == null)
			return false;

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		boolean this_present_columnName = true && this.isSetColumnName();
		boolean that_present_columnName = true && that.isSetColumnName();
		if (this_present_columnName || that_present_columnName) {
			if (!(this_present_columnName && that_present_columnName))
				return false;
			if (!this.columnName.equals(that.columnName))
				return false;
		}

		boolean this_present_columnIndex = true;
		boolean that_present_columnIndex = true;
		if (this_present_columnIndex || that_present_columnIndex) {
			if (!(this_present_columnIndex && that_present_columnIndex))
				return false;
			if (this.columnIndex != that.columnIndex)
				return false;
		}

		boolean this_present_sampleValue = true && this.isSetSampleValue();
		boolean that_present_sampleValue = true && that.isSetSampleValue();
		if (this_present_sampleValue || that_present_sampleValue) {
			if (!(this_present_sampleValue && that_present_sampleValue))
				return false;
			if (!this.sampleValue.equals(that.sampleValue))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type);

		boolean present_columnName = true && (isSetColumnName());
		builder.append(present_columnName);
		if (present_columnName)
			builder.append(columnName);

		boolean present_columnIndex = true;
		builder.append(present_columnIndex);
		if (present_columnIndex)
			builder.append(columnIndex);

		boolean present_sampleValue = true && (isSetSampleValue());
		builder.append(present_sampleValue);
		if (present_sampleValue)
			builder.append(sampleValue);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FieldMapping(");
		boolean first = true;

		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (isSetColumnName()) {
			if (!first) sb.append(", ");
			sb.append("columnName:");
			if (this.columnName == null) {
				sb.append("null");
			} else {
				sb.append(this.columnName);
			}
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("columnIndex:");
		sb.append(this.columnIndex);
		first = false;
		if (isSetSampleValue()) {
			if (!first) sb.append(", ");
			sb.append("sampleValue:");
			if (this.sampleValue == null) {
				sb.append("null");
			} else {
				sb.append(this.sampleValue);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class CustomFieldReportRow implements Serializable {
	private static final long serialVersionUID = 1L;

	private long fieldId;
	private String fieldName;
	private String fieldValue;

	public CustomFieldReportRow() {
	}

	public long getFieldId() {
		return this.fieldId;
	}

	public CustomFieldReportRow setFieldId(long fieldId) {
		this.fieldId = fieldId;
		return this;
	}

	public boolean isSetFieldId() {
		return (fieldId > 0L);
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public CustomFieldReportRow setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public boolean isSetFieldName() {
		return this.fieldName != null;
	}

	public String getFieldValue() {
		return this.fieldValue;
	}

	public CustomFieldReportRow setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
		return this;
	}

	public boolean isSetFieldValue() {
		return this.fieldValue != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof CustomFieldReportRow)
			return this.equals((CustomFieldReportRow) that);
		return false;
	}

	private boolean equals(CustomFieldReportRow that) {
		if (that == null)
			return false;

		boolean this_present_fieldId = true && this.isSetFieldId();
		boolean that_present_fieldId = true && that.isSetFieldId();
		if (this_present_fieldId || that_present_fieldId) {
			if (!(this_present_fieldId && that_present_fieldId))
				return false;
			if (this.fieldId != that.fieldId)
				return false;
		}

		boolean this_present_fieldName = true && this.isSetFieldName();
		boolean that_present_fieldName = true && that.isSetFieldName();
		if (this_present_fieldName || that_present_fieldName) {
			if (!(this_present_fieldName && that_present_fieldName))
				return false;
			if (!this.fieldName.equals(that.fieldName))
				return false;
		}

		boolean this_present_fieldValue = true && this.isSetFieldValue();
		boolean that_present_fieldValue = true && that.isSetFieldValue();
		if (this_present_fieldValue || that_present_fieldValue) {
			if (!(this_present_fieldValue && that_present_fieldValue))
				return false;
			if (!this.fieldValue.equals(that.fieldValue))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_fieldId = true && (isSetFieldId());
		builder.append(present_fieldId);
		if (present_fieldId)
			builder.append(fieldId);

		boolean present_fieldName = true && (isSetFieldName());
		builder.append(present_fieldName);
		if (present_fieldName)
			builder.append(fieldName);

		boolean present_fieldValue = true && (isSetFieldValue());
		builder.append(present_fieldValue);
		if (present_fieldValue)
			builder.append(fieldValue);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("CustomFieldReportRow(");
		boolean first = true;

		if (isSetFieldId()) {
			sb.append("fieldId:");
			sb.append(this.fieldId);
			first = false;
		}
		if (isSetFieldName()) {
			if (!first) sb.append(", ");
			sb.append("fieldName:");
			if (this.fieldName == null) {
				sb.append("null");
			} else {
				sb.append(this.fieldName);
			}
			first = false;
		}
		if (isSetFieldValue()) {
			if (!first) sb.append(", ");
			sb.append("fieldValue:");
			if (this.fieldValue == null) {
				sb.append("null");
			} else {
				sb.append(this.fieldValue);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}


package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkUploadValue implements Serializable {
	private static final long serialVersionUID = 1L;

	private FieldType type;
	private String value;
	private boolean fromTemplate;

	public WorkUploadValue() {
	}

	public WorkUploadValue(FieldType type, String value, boolean fromTemplate) {
		this();
		this.type = type;
		this.value = value;
		this.fromTemplate = fromTemplate;
	}

	public FieldType getType() {
		return this.type;
	}

	public WorkUploadValue setType(FieldType type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public String getValue() {
		return this.value;
	}

	public WorkUploadValue setValue(String value) {
		this.value = value;
		return this;
	}

	public boolean isSetValue() {
		return this.value != null;
	}

	public boolean isFromTemplate() {
		return this.fromTemplate;
	}

	public WorkUploadValue setFromTemplate(boolean fromTemplate) {
		this.fromTemplate = fromTemplate;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkUploadValue)
			return this.equals((WorkUploadValue) that);
		return false;
	}

	private boolean equals(WorkUploadValue that) {
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

		boolean this_present_value = true && this.isSetValue();
		boolean that_present_value = true && that.isSetValue();
		if (this_present_value || that_present_value) {
			if (!(this_present_value && that_present_value))
				return false;
			if (!this.value.equals(that.value))
				return false;
		}

		boolean this_present_fromTemplate = true;
		boolean that_present_fromTemplate = true;
		if (this_present_fromTemplate || that_present_fromTemplate) {
			if (!(this_present_fromTemplate && that_present_fromTemplate))
				return false;
			if (this.fromTemplate != that.fromTemplate)
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

		boolean present_value = true && (isSetValue());
		builder.append(present_value);
		if (present_value)
			builder.append(value);

		boolean present_fromTemplate = true;
		builder.append(present_fromTemplate);
		if (present_fromTemplate)
			builder.append(fromTemplate);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkUploadValue(");
		boolean first = true;

		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("value:");
		if (this.value == null) {
			sb.append("null");
		} else {
			sb.append(this.value);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("fromTemplate:");
		sb.append(this.fromTemplate);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
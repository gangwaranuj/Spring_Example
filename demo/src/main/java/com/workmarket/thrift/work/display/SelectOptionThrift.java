package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class SelectOptionThrift implements Serializable {
	private static final long serialVersionUID = 1L;

	private String value;
	private String label;

	public SelectOptionThrift() {
	}

	public SelectOptionThrift(String value, String label) {
		this();
		this.value = value;
		this.label = label;
	}

	public String getValue() {
		return this.value;
	}

	public SelectOptionThrift setValue(String value) {
		this.value = value;
		return this;
	}

	public boolean isSetValue() {
		return this.value != null;
	}

	public String getLabel() {
		return this.label;
	}

	public SelectOptionThrift setLabel(String label) {
		this.label = label;
		return this;
	}

	public boolean isSetLabel() {
		return this.label != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SelectOptionThrift)
			return this.equals((SelectOptionThrift) that);
		return false;
	}

	private boolean equals(SelectOptionThrift that) {
		if (that == null)
			return false;

		boolean this_present_value = true && this.isSetValue();
		boolean that_present_value = true && that.isSetValue();
		if (this_present_value || that_present_value) {
			if (!(this_present_value && that_present_value))
				return false;
			if (!this.value.equals(that.value))
				return false;
		}

		boolean this_present_label = true && this.isSetLabel();
		boolean that_present_label = true && that.isSetLabel();
		if (this_present_label || that_present_label) {
			if (!(this_present_label && that_present_label))
				return false;
			if (!this.label.equals(that.label))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_value = true && (isSetValue());
		builder.append(present_value);
		if (present_value)
			builder.append(value);

		boolean present_label = true && (isSetLabel());
		builder.append(present_label);
		if (present_label)
			builder.append(label);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SelectOptionThrift(");
		boolean first = true;

		sb.append("value:");
		if (this.value == null) {
			sb.append("null");
		} else {
			sb.append(this.value);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("label:");
		if (this.label == null) {
			sb.append("null");
		} else {
			sb.append(this.label);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


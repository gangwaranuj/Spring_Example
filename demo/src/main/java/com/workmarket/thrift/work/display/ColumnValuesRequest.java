package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class ColumnValuesRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String keyName;
	private String value;

	public ColumnValuesRequest() {
	}

	public ColumnValuesRequest(String keyName, String value) {
		this();
		this.keyName = keyName;
		this.value = value;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public ColumnValuesRequest setKeyName(String keyName) {
		this.keyName = keyName;
		return this;
	}

	public boolean isSetKeyName() {
		return this.keyName != null;
	}

	public String getValue() {
		return this.value;
	}

	public ColumnValuesRequest setValue(String value) {
		this.value = value;
		return this;
	}

	public boolean isSetValue() {
		return this.value != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ColumnValuesRequest)
			return this.equals((ColumnValuesRequest) that);
		return false;
	}

	private boolean equals(ColumnValuesRequest that) {
		if (that == null)
			return false;

		boolean this_present_keyName = true && this.isSetKeyName();
		boolean that_present_keyName = true && that.isSetKeyName();
		if (this_present_keyName || that_present_keyName) {
			if (!(this_present_keyName && that_present_keyName))
				return false;
			if (!this.keyName.equals(that.keyName))
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

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_keyName = true && (isSetKeyName());
		builder.append(present_keyName);
		if (present_keyName)
			builder.append(keyName);

		boolean present_value = true && (isSetValue());
		builder.append(present_value);
		if (present_value)
			builder.append(value);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ColumnValuesRequest(");
		boolean first = true;

		sb.append("keyName:");
		if (this.keyName == null) {
			sb.append("null");
		} else {
			sb.append(this.keyName);
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
		sb.append(")");
		return sb.toString();
	}


}
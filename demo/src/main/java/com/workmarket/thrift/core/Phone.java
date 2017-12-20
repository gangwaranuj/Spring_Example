package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Phone implements Serializable {
	private static final long serialVersionUID = 1L;

	private String phone;
	private String extension;
	private String type;

	public Phone() {
	}

	public Phone(
			String phone,
			String extension,
			String type) {
		this();
		this.phone = phone;
		this.extension = extension;
		this.type = type;
	}

	public String getPhone() {
		return this.phone;
	}

	public Phone setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public boolean isSetPhone() {
		return this.phone != null;
	}

	public String getExtension() {
		return this.extension;
	}

	public Phone setExtension(String extension) {
		this.extension = extension;
		return this;
	}

	public boolean isSetExtension() {
		return this.extension != null;
	}

	public String getType() {
		return this.type;
	}

	public Phone setType(String type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Phone)
			return this.equals((Phone) that);
		return false;
	}

	private boolean equals(Phone that) {
		if (that == null)
			return false;

		boolean this_present_phone = true && this.isSetPhone();
		boolean that_present_phone = true && that.isSetPhone();
		if (this_present_phone || that_present_phone) {
			if (!(this_present_phone && that_present_phone))
				return false;
			if (!this.phone.equals(that.phone))
				return false;
		}

		boolean this_present_extension = true && this.isSetExtension();
		boolean that_present_extension = true && that.isSetExtension();
		if (this_present_extension || that_present_extension) {
			if (!(this_present_extension && that_present_extension))
				return false;
			if (!this.extension.equals(that.extension))
				return false;
		}

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_phone = true && (isSetPhone());
		builder.append(present_phone);
		if (present_phone)
			builder.append(phone);

		boolean present_extension = true && (isSetExtension());
		builder.append(present_extension);
		if (present_extension)
			builder.append(extension);

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Phone(");
		boolean first = true;

		sb.append("phone:");
		if (this.phone == null) {
			sb.append("null");
		} else {
			sb.append(this.phone);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("extension:");
		if (this.extension == null) {
			sb.append("null");
		} else {
			sb.append(this.extension);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


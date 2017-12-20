package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Status implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String description;
	private String note;

	public Status() {
	}

	public Status(String code, String description, String note) {
		this();
		this.code = code;
		this.description = description;
		this.note = note;
	}

	public String getCode() {
		return this.code;
	}

	public Status setCode(String code) {
		this.code = code;
		return this;
	}

	public boolean isSetCode() {
		return this.code != null;
	}

	public String getDescription() {
		return this.description;
	}

	public Status setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public String getNote() {
		return this.note;
	}

	public Status setNote(String note) {
		this.note = note;
		return this;
	}

	public boolean isSetNote() {
		return this.note != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Status)
			return this.equals((Status) that);
		return false;
	}

	private boolean equals(Status that) {
		if (that == null)
			return false;

		boolean this_present_code = true && this.isSetCode();
		boolean that_present_code = true && that.isSetCode();
		if (this_present_code || that_present_code) {
			if (!(this_present_code && that_present_code))
				return false;
			if (!this.code.equals(that.code))
				return false;
		}

		boolean this_present_description = true && this.isSetDescription();
		boolean that_present_description = true && that.isSetDescription();
		if (this_present_description || that_present_description) {
			if (!(this_present_description && that_present_description))
				return false;
			if (!this.description.equals(that.description))
				return false;
		}

		boolean this_present_note = true && this.isSetNote();
		boolean that_present_note = true && that.isSetNote();
		if (this_present_note || that_present_note) {
			if (!(this_present_note && that_present_note))
				return false;
			if (!this.note.equals(that.note))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_code = true && (isSetCode());
		builder.append(present_code);
		if (present_code)
			builder.append(code);

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		boolean present_note = true && (isSetNote());
		builder.append(present_note);
		if (present_note)
			builder.append(note);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Status(");
		boolean first = true;

		sb.append("code:");
		if (this.code == null) {
			sb.append("null");
		} else {
			sb.append(this.code);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("description:");
		if (this.description == null) {
			sb.append("null");
		} else {
			sb.append(this.description);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("note:");
		if (this.note == null) {
			sb.append("null");
		} else {
			sb.append(this.note);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


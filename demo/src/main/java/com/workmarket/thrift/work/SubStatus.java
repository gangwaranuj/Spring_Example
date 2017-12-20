package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class SubStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String description;
	private String note;
	private boolean userResolvable;
	private String colorRgb;
	private long id;

	public SubStatus() {
	}

	public SubStatus(String code, String description, String note, boolean userResolvable, long id) {
		this();
		this.code = code;
		this.description = description;
		this.note = note;
		this.userResolvable = userResolvable;
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public SubStatus setCode(String code) {
		this.code = code;
		return this;
	}

	public boolean isSetCode() {
		return this.code != null;
	}

	public String getDescription() {
		return this.description;
	}

	public SubStatus setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public String getNote() {
		return this.note;
	}

	public SubStatus setNote(String note) {
		this.note = note;
		return this;
	}

	public boolean isSetNote() {
		return this.note != null;
	}

	public boolean isUserResolvable() {
		return this.userResolvable;
	}

	public SubStatus setUserResolvable(boolean userResolvable) {
		this.userResolvable = userResolvable;
		return this;
	}

	public String getColorRgb() {
		return this.colorRgb;
	}

	public SubStatus setColorRgb(String colorRgb) {
		this.colorRgb = colorRgb;
		return this;
	}

	public boolean isSetColorRgb() {
		return this.colorRgb != null;
	}

	public long getId() {
		return this.id;
	}

	public SubStatus setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SubStatus)
			return this.equals((SubStatus) that);
		return false;
	}

	private boolean equals(SubStatus that) {
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

		boolean this_present_userResolvable = true;
		boolean that_present_userResolvable = true;
		if (this_present_userResolvable || that_present_userResolvable) {
			if (!(this_present_userResolvable && that_present_userResolvable))
				return false;
			if (this.userResolvable != that.userResolvable)
				return false;
		}

		boolean this_present_colorRgb = true && this.isSetColorRgb();
		boolean that_present_colorRgb = true && that.isSetColorRgb();
		if (this_present_colorRgb || that_present_colorRgb) {
			if (!(this_present_colorRgb && that_present_colorRgb))
				return false;
			if (!this.colorRgb.equals(that.colorRgb))
				return false;
		}

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
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

		boolean present_userResolvable = true;
		builder.append(present_userResolvable);
		if (present_userResolvable)
			builder.append(userResolvable);

		boolean present_colorRgb = true && (isSetColorRgb());
		builder.append(present_colorRgb);
		if (present_colorRgb)
			builder.append(colorRgb);

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SubStatus(");
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
		if (!first) sb.append(", ");
		sb.append("userResolvable:");
		sb.append(this.userResolvable);
		first = false;
		if (isSetColorRgb()) {
			if (!first) sb.append(", ");
			sb.append("colorRgb:");
			if (this.colorRgb == null) {
				sb.append("null");
			} else {
				sb.append(this.colorRgb);
			}
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("id:");
		sb.append(this.id);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Choice implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private int position;
	private String value;
	private boolean correct;

	public Choice() {
	}

	public Choice(long id, int position, String value, boolean correct) {
		this();
		this.id = id;
		this.position = position;
		this.value = value;
		this.correct = correct;
	}

	public long getId() {
		return this.id;
	}

	public Choice setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public int getPosition() {
		return this.position;
	}

	public Choice setPosition(int position) {
		this.position = position;
		return this;
	}

	public boolean isSetPosition() {
		return (position > 0);
	}

	public String getValue() {
		return this.value;
	}

	public Choice setValue(String value) {
		this.value = value;
		return this;
	}

	public boolean isSetValue() {
		return this.value != null;
	}

	public boolean isCorrect() {
		return this.correct;
	}

	public Choice setCorrect(boolean correct) {
		this.correct = correct;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Choice)
			return this.equals((Choice) that);
		return false;
	}

	private boolean equals(Choice that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_position = true;
		boolean that_present_position = true;
		if (this_present_position || that_present_position) {
			if (!(this_present_position && that_present_position))
				return false;
			if (this.position != that.position)
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

		boolean this_present_correct = true;
		boolean that_present_correct = true;
		if (this_present_correct || that_present_correct) {
			if (!(this_present_correct && that_present_correct))
				return false;
			if (this.correct != that.correct)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_position = true;
		builder.append(present_position);
		if (present_position)
			builder.append(position);

		boolean present_value = true && (isSetValue());
		builder.append(present_value);
		if (present_value)
			builder.append(value);

		boolean present_correct = true;
		builder.append(present_correct);
		if (present_correct)
			builder.append(correct);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Choice(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("position:");
		sb.append(this.position);
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
		sb.append("correct:");
		sb.append(this.correct);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
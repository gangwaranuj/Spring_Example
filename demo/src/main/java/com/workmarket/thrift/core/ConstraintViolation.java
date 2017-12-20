package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class ConstraintViolation implements Serializable {
	private static final long serialVersionUID = 1L;

	private String why;
	private String property;
	private String error;
	private List<String> params;

	public ConstraintViolation() {
	}

	public ConstraintViolation(String why, String property, String error, List<String> params) {
		this();
		this.why = why;
		this.property = property;
		this.error = error;
		this.params = params;
	}

	public String getWhy() {
		return this.why;
	}

	public ConstraintViolation setWhy(String why) {
		this.why = why;
		return this;
	}

	public boolean isSetWhy() {
		return this.why != null;
	}

	public String getProperty() {
		return this.property;
	}

	public ConstraintViolation setProperty(String property) {
		this.property = property;
		return this;
	}

	public boolean isSetProperty() {
		return this.property != null;
	}

	public String getError() {
		return this.error;
	}

	public ConstraintViolation setError(String error) {
		this.error = error;
		return this;
	}

	public boolean isSetError() {
		return this.error != null;
	}

	public int getParamsSize() {
		return (this.params == null) ? 0 : this.params.size();
	}

	public ConstraintViolation addToParams(String elem) {
		if (this.params == null) {
			this.params = new ArrayList<>();
		}
		this.params.add(elem);
		return this;
	}

	public ConstraintViolation addToParams(Collection<String> elem) {
		if (isEmpty(elem)) {
			return this;
		}
		if (this.params == null) {
			this.params = new ArrayList<>();
		}
		this.params.addAll(elem);
		return this;
	}

	public List<String> getParams() {
		return this.params;
	}

	public ConstraintViolation setParams(List<String> params) {
		this.params = params;
		return this;
	}

	public boolean isSetParams() {
		return this.params != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ConstraintViolation)
			return this.equals((ConstraintViolation) that);
		return false;
	}

	private boolean equals(ConstraintViolation that) {
		if (that == null)
			return false;

		boolean this_present_why = true && this.isSetWhy();
		boolean that_present_why = true && that.isSetWhy();
		if (this_present_why || that_present_why) {
			if (!(this_present_why && that_present_why))
				return false;
			if (!this.why.equals(that.why))
				return false;
		}

		boolean this_present_property = true && this.isSetProperty();
		boolean that_present_property = true && that.isSetProperty();
		if (this_present_property || that_present_property) {
			if (!(this_present_property && that_present_property))
				return false;
			if (!this.property.equals(that.property))
				return false;
		}

		boolean this_present_error = true && this.isSetError();
		boolean that_present_error = true && that.isSetError();
		if (this_present_error || that_present_error) {
			if (!(this_present_error && that_present_error))
				return false;
			if (!this.error.equals(that.error))
				return false;
		}

		boolean this_present_params = true && this.isSetParams();
		boolean that_present_params = true && that.isSetParams();
		if (this_present_params || that_present_params) {
			if (!(this_present_params && that_present_params))
				return false;
			if (!this.params.equals(that.params))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_why = true && (isSetWhy());
		builder.append(present_why);
		if (present_why)
			builder.append(why);

		boolean present_property = true && (isSetProperty());
		builder.append(present_property);
		if (present_property)
			builder.append(property);

		boolean present_error = true && (isSetError());
		builder.append(present_error);
		if (present_error)
			builder.append(error);

		boolean present_params = true && (isSetParams());
		builder.append(present_params);
		if (present_params)
			builder.append(params);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ConstraintViolation(");
		boolean first = true;

		sb.append("why:");
		if (this.why == null) {
			sb.append("null");
		} else {
			sb.append(this.why);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("property:");
		if (this.property == null) {
			sb.append("null");
		} else {
			sb.append(this.property);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("error:");
		if (this.error == null) {
			sb.append("null");
		} else {
			sb.append(this.error);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("params:");
		if (this.params == null) {
			sb.append("null");
		} else {
			sb.append(this.params);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


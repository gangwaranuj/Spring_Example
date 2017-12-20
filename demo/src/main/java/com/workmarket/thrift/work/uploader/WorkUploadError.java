package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkUploadError implements Serializable {
	private static final long serialVersionUID = 1L;

	private WorkUploadErrorType errorType;
	private com.workmarket.thrift.core.ConstraintViolation violation;
	private String column;

	public WorkUploadError() {
	}

	public WorkUploadError(
			WorkUploadErrorType errorType,
			com.workmarket.thrift.core.ConstraintViolation violation,
			String column) {
		this();
		this.errorType = errorType;
		this.violation = violation;
		this.column = column;
	}

	public WorkUploadErrorType getErrorType() {
		return this.errorType;
	}

	public WorkUploadError setErrorType(WorkUploadErrorType errorType) {
		this.errorType = errorType;
		return this;
	}

	public boolean isSetErrorType() {
		return this.errorType != null;
	}

	public com.workmarket.thrift.core.ConstraintViolation getViolation() {
		return this.violation;
	}

	public WorkUploadError setViolation(com.workmarket.thrift.core.ConstraintViolation violation) {
		this.violation = violation;
		return this;
	}

	public boolean isSetViolation() {
		return this.violation != null;
	}

	public String getColumn() {
		return this.column;
	}

	public WorkUploadError setColumn(String column) {
		this.column = column;
		return this;
	}

	public boolean isSetColumn() {
		return this.column != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkUploadError)
			return this.equals((WorkUploadError) that);
		return false;
	}

	public boolean equals(WorkUploadError that) {
		if (that == null)
			return false;

		boolean this_present_errorType = true && this.isSetErrorType();
		boolean that_present_errorType = true && that.isSetErrorType();
		if (this_present_errorType || that_present_errorType) {
			if (!(this_present_errorType && that_present_errorType))
				return false;
			if (!this.errorType.equals(that.errorType))
				return false;
		}

		boolean this_present_violation = true && this.isSetViolation();
		boolean that_present_violation = true && that.isSetViolation();
		if (this_present_violation || that_present_violation) {
			if (!(this_present_violation && that_present_violation))
				return false;
			if (!this.violation.equals(that.violation))
				return false;
		}

		boolean this_present_column = true && this.isSetColumn();
		boolean that_present_column = true && that.isSetColumn();
		if (this_present_column || that_present_column) {
			if (!(this_present_column && that_present_column))
				return false;
			if (!this.column.equals(that.column))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_errorType = true && (isSetErrorType());
		builder.append(present_errorType);
		if (present_errorType)
			builder.append(errorType.getValue());

		boolean present_violation = true && (isSetViolation());
		builder.append(present_violation);
		if (present_violation)
			builder.append(violation);

		boolean present_column = true && (isSetColumn());
		builder.append(present_column);
		if (present_column)
			builder.append(column);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkUploadError(");
		boolean first = true;

		sb.append("errorType:");
		if (this.errorType == null) {
			sb.append("null");
		} else {
			sb.append(this.errorType);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("violation:");
		if (this.violation == null) {
			sb.append("null");
		} else {
			sb.append(this.violation);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("column:");
		if (this.column == null) {
			sb.append("null");
		} else {
			sb.append(this.column);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
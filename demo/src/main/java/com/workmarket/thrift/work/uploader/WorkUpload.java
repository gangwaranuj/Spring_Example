package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkUpload implements Serializable {
	private static final long serialVersionUID = 1L;

	private String workNumber;
	private int lineNumber;
	private List<WorkUploadValue> values;
	private List<WorkUploadError> errors;

	public WorkUpload() {
	}

	public WorkUpload(
			String workNumber,
			int lineNumber,
			List<WorkUploadValue> values,
			List<WorkUploadError> errors) {
		this();
		this.workNumber = workNumber;
		this.lineNumber = lineNumber;
		this.values = values;
		this.errors = errors;
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public WorkUpload setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	public WorkUpload setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
		return this;
	}

	public int getValuesSize() {
		return (this.values == null) ? 0 : this.values.size();
	}

	public java.util.Iterator<WorkUploadValue> getValuesIterator() {
		return (this.values == null) ? null : this.values.iterator();
	}

	public void addToValues(WorkUploadValue elem) {
		if (this.values == null) {
			this.values = new ArrayList<WorkUploadValue>();
		}
		this.values.add(elem);
	}

	public List<WorkUploadValue> getValues() {
		return this.values;
	}

	public WorkUpload setValues(List<WorkUploadValue> values) {
		this.values = values;
		return this;
	}

	public boolean isSetValues() {
		return this.values != null;
	}

	public int getErrorsSize() {
		return (this.errors == null) ? 0 : this.errors.size();
	}

	public java.util.Iterator<WorkUploadError> getErrorsIterator() {
		return (this.errors == null) ? null : this.errors.iterator();
	}

	public void addToErrors(WorkUploadError elem) {
		if (this.errors == null) {
			this.errors = new ArrayList<WorkUploadError>();
		}
		this.errors.add(elem);
	}

	public List<WorkUploadError> getErrors() {
		return this.errors;
	}

	public WorkUpload setErrors(List<WorkUploadError> errors) {
		this.errors = errors;
		return this;
	}

	public boolean isSetErrors() {
		return this.errors != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkUpload)
			return this.equals((WorkUpload) that);
		return false;
	}

	private boolean equals(WorkUpload that) {
		if (that == null)
			return false;

		boolean this_present_workNumber = true && this.isSetWorkNumber();
		boolean that_present_workNumber = true && that.isSetWorkNumber();
		if (this_present_workNumber || that_present_workNumber) {
			if (!(this_present_workNumber && that_present_workNumber))
				return false;
			if (!this.workNumber.equals(that.workNumber))
				return false;
		}

		boolean this_present_lineNumber = true;
		boolean that_present_lineNumber = true;
		if (this_present_lineNumber || that_present_lineNumber) {
			if (!(this_present_lineNumber && that_present_lineNumber))
				return false;
			if (this.lineNumber != that.lineNumber)
				return false;
		}

		boolean this_present_values = true && this.isSetValues();
		boolean that_present_values = true && that.isSetValues();
		if (this_present_values || that_present_values) {
			if (!(this_present_values && that_present_values))
				return false;
			if (!this.values.equals(that.values))
				return false;
		}

		boolean this_present_errors = true && this.isSetErrors();
		boolean that_present_errors = true && that.isSetErrors();
		if (this_present_errors || that_present_errors) {
			if (!(this_present_errors && that_present_errors))
				return false;
			if (!this.errors.equals(that.errors))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber)
			builder.append(workNumber);

		boolean present_lineNumber = true;
		builder.append(present_lineNumber);
		if (present_lineNumber)
			builder.append(lineNumber);

		boolean present_values = true && (isSetValues());
		builder.append(present_values);
		if (present_values)
			builder.append(values);

		boolean present_errors = true && (isSetErrors());
		builder.append(present_errors);
		if (present_errors)
			builder.append(errors);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkUpload(");
		boolean first = true;

		sb.append("workNumber:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("lineNumber:");
		sb.append(this.lineNumber);
		first = false;
		if (!first) sb.append(", ");
		sb.append("values:");
		if (this.values == null) {
			sb.append("null");
		} else {
			sb.append(this.values);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("errors:");
		if (this.errors == null) {
			sb.append("null");
		} else {
			sb.append(this.errors);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
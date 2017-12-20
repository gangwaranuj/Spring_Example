package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportRow implements Serializable {
	private static final long serialVersionUID = 1L;

	private int rowNumber;
	private List<String> reportFields;

	public ReportRow() {
	}

	public ReportRow(int rowNumber, List<String> reportFields) {
		this();
		this.rowNumber = rowNumber;
		this.reportFields = reportFields;
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public ReportRow setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
		return this;
	}

	public int getReportFieldsSize() {
		return (this.reportFields == null) ? 0 : this.reportFields.size();
	}

	public java.util.Iterator<String> getReportFieldsIterator() {
		return (this.reportFields == null) ? null : this.reportFields.iterator();
	}

	public void addToReportFields(String elem) {
		if (this.reportFields == null) {
			this.reportFields = new ArrayList<String>();
		}
		this.reportFields.add(elem);
	}

	public List<String> getReportFields() {
		return this.reportFields;
	}

	public ReportRow setReportFields(List<String> reportFields) {
		this.reportFields = reportFields;
		return this;
	}

	public boolean isSetReportFields() {
		return this.reportFields != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ReportRow)
			return this.equals((ReportRow) that);
		return false;
	}

	private boolean equals(ReportRow that) {
		if (that == null)
			return false;

		boolean this_present_rowNumber = true;
		boolean that_present_rowNumber = true;
		if (this_present_rowNumber || that_present_rowNumber) {
			if (!(this_present_rowNumber && that_present_rowNumber))
				return false;
			if (this.rowNumber != that.rowNumber)
				return false;
		}

		boolean this_present_reportFields = true && this.isSetReportFields();
		boolean that_present_reportFields = true && that.isSetReportFields();
		if (this_present_reportFields || that_present_reportFields) {
			if (!(this_present_reportFields && that_present_reportFields))
				return false;
			if (!this.reportFields.equals(that.reportFields))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_rowNumber = true;
		builder.append(present_rowNumber);
		if (present_rowNumber)
			builder.append(rowNumber);

		boolean present_reportFields = true && (isSetReportFields());
		builder.append(present_reportFields);
		if (present_reportFields)
			builder.append(reportFields);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ReportRow(");
		boolean first = true;

		sb.append("rowNumber:");
		sb.append(this.rowNumber);
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportFields:");
		if (this.reportFields == null) {
			sb.append("null");
		} else {
			sb.append(this.reportFields);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
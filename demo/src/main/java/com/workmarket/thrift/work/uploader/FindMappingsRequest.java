package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class FindMappingsRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long companyId;
	private int startRow;
	private int resultsLimit;

	public FindMappingsRequest() {
		this.startRow = 0;
		this.resultsLimit = 10;
	}

	public FindMappingsRequest(long companyId, int startRow, int resultsLimit) {
		this();
		this.companyId = companyId;
		this.startRow = startRow;
		this.resultsLimit = resultsLimit;
	}

	public long getCompanyId() {
		return this.companyId;
	}

	public FindMappingsRequest setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	public boolean isSetCompanyId() {
		return (companyId > 0L);
	}

	public int getStartRow() {
		return this.startRow;
	}

	public FindMappingsRequest setStartRow(int startRow) {
		this.startRow = startRow;
		return this;
	}

	public int getResultsLimit() {
		return this.resultsLimit;
	}

	public FindMappingsRequest setResultsLimit(int resultsLimit) {
		this.resultsLimit = resultsLimit;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FindMappingsRequest)
			return this.equals((FindMappingsRequest) that);
		return false;
	}

	private boolean equals(FindMappingsRequest that) {
		if (that == null)
			return false;

		boolean this_present_companyId = true;
		boolean that_present_companyId = true;
		if (this_present_companyId || that_present_companyId) {
			if (!(this_present_companyId && that_present_companyId))
				return false;
			if (this.companyId != that.companyId)
				return false;
		}

		boolean this_present_startRow = true;
		boolean that_present_startRow = true;
		if (this_present_startRow || that_present_startRow) {
			if (!(this_present_startRow && that_present_startRow))
				return false;
			if (this.startRow != that.startRow)
				return false;
		}

		boolean this_present_resultsLimit = true;
		boolean that_present_resultsLimit = true;
		if (this_present_resultsLimit || that_present_resultsLimit) {
			if (!(this_present_resultsLimit && that_present_resultsLimit))
				return false;
			if (this.resultsLimit != that.resultsLimit)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_companyId = true;
		builder.append(present_companyId);
		if (present_companyId)
			builder.append(companyId);

		boolean present_startRow = true;
		builder.append(present_startRow);
		if (present_startRow)
			builder.append(startRow);

		boolean present_resultsLimit = true;
		builder.append(present_resultsLimit);
		if (present_resultsLimit)
			builder.append(resultsLimit);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FindMappingsRequest(");
		boolean first = true;

		sb.append("companyId:");
		sb.append(this.companyId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("startRow:");
		sb.append(this.startRow);
		first = false;
		if (!first) sb.append(", ");
		sb.append("resultsLimit:");
		sb.append(this.resultsLimit);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FindMappingsResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<FieldMappingGroup> mappingGroups;
	private long numResults;

	public FindMappingsResponse() {
	}

	public FindMappingsResponse(List<FieldMappingGroup> mappingGroups, long numResults) {
		this();
		this.mappingGroups = mappingGroups;
		this.numResults = numResults;
	}

	public int getMappingGroupsSize() {
		return (this.mappingGroups == null) ? 0 : this.mappingGroups.size();
	}

	public java.util.Iterator<FieldMappingGroup> getMappingGroupsIterator() {
		return (this.mappingGroups == null) ? null : this.mappingGroups.iterator();
	}

	public void addToMappingGroups(FieldMappingGroup elem) {
		if (this.mappingGroups == null) {
			this.mappingGroups = new ArrayList<FieldMappingGroup>();
		}
		this.mappingGroups.add(elem);
	}

	public List<FieldMappingGroup> getMappingGroups() {
		return this.mappingGroups;
	}

	public FindMappingsResponse setMappingGroups(List<FieldMappingGroup> mappingGroups) {
		this.mappingGroups = mappingGroups;
		return this;
	}

	public boolean isSetMappingGroups() {
		return this.mappingGroups != null;
	}

	public long getNumResults() {
		return this.numResults;
	}

	public FindMappingsResponse setNumResults(long numResults) {
		this.numResults = numResults;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FindMappingsResponse)
			return this.equals((FindMappingsResponse) that);
		return false;
	}

	private boolean equals(FindMappingsResponse that) {
		if (that == null)
			return false;

		boolean this_present_mappingGroups = true && this.isSetMappingGroups();
		boolean that_present_mappingGroups = true && that.isSetMappingGroups();
		if (this_present_mappingGroups || that_present_mappingGroups) {
			if (!(this_present_mappingGroups && that_present_mappingGroups))
				return false;
			if (!this.mappingGroups.equals(that.mappingGroups))
				return false;
		}

		boolean this_present_numResults = true;
		boolean that_present_numResults = true;
		if (this_present_numResults || that_present_numResults) {
			if (!(this_present_numResults && that_present_numResults))
				return false;
			if (this.numResults != that.numResults)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_mappingGroups = true && (isSetMappingGroups());
		builder.append(present_mappingGroups);
		if (present_mappingGroups)
			builder.append(mappingGroups);

		boolean present_numResults = true;
		builder.append(present_numResults);
		if (present_numResults)
			builder.append(numResults);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FindMappingsResponse(");
		boolean first = true;

		sb.append("mappingGroups:");
		if (this.mappingGroups == null) {
			sb.append("null");
		} else {
			sb.append(this.mappingGroups);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("numResults:");
		sb.append(this.numResults);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
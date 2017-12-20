package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RenameMappingRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private long mappingGroupId;
	private String name;

	public RenameMappingRequest() {
	}

	public RenameMappingRequest(String userNumber, long mappingGroupId, String name) {
		this();
		this.userNumber = userNumber;
		this.mappingGroupId = mappingGroupId;
		this.name = name;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public RenameMappingRequest setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public long getMappingGroupId() {
		return this.mappingGroupId;
	}

	public RenameMappingRequest setMappingGroupId(long mappingGroupId) {
		this.mappingGroupId = mappingGroupId;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public RenameMappingRequest setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RenameMappingRequest)
			return this.equals((RenameMappingRequest) that);
		return false;
	}

	private boolean equals(RenameMappingRequest that) {
		if (that == null)
			return false;

		boolean this_present_userNumber = true && this.isSetUserNumber();
		boolean that_present_userNumber = true && that.isSetUserNumber();
		if (this_present_userNumber || that_present_userNumber) {
			if (!(this_present_userNumber && that_present_userNumber))
				return false;
			if (!this.userNumber.equals(that.userNumber))
				return false;
		}

		boolean this_present_mappingGroupId = true;
		boolean that_present_mappingGroupId = true;
		if (this_present_mappingGroupId || that_present_mappingGroupId) {
			if (!(this_present_mappingGroupId && that_present_mappingGroupId))
				return false;
			if (this.mappingGroupId != that.mappingGroupId)
				return false;
		}

		boolean this_present_name = true && this.isSetName();
		boolean that_present_name = true && that.isSetName();
		if (this_present_name || that_present_name) {
			if (!(this_present_name && that_present_name))
				return false;
			if (!this.name.equals(that.name))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userNumber = true && (isSetUserNumber());
		builder.append(present_userNumber);
		if (present_userNumber)
			builder.append(userNumber);

		boolean present_mappingGroupId = true;
		builder.append(present_mappingGroupId);
		if (present_mappingGroupId)
			builder.append(mappingGroupId);

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RenameMappingRequest(");
		boolean first = true;

		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("mappingGroupId:");
		sb.append(this.mappingGroupId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
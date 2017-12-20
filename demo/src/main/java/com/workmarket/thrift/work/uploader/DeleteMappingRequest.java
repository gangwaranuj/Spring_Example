package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DeleteMappingRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private long mappingGroupId;

	public DeleteMappingRequest() {
	}

	public DeleteMappingRequest(String userNumber, long mappingGroupId) {
		this();
		this.userNumber = userNumber;
		this.mappingGroupId = mappingGroupId;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public DeleteMappingRequest setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public long getMappingGroupId() {
		return this.mappingGroupId;
	}

	public DeleteMappingRequest setMappingGroupId(long mappingGroupId) {
		this.mappingGroupId = mappingGroupId;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DeleteMappingRequest)
			return this.equals((DeleteMappingRequest) that);
		return false;
	}

	private boolean equals(DeleteMappingRequest that) {
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

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DeleteMappingRequest(");
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
		sb.append(")");
		return sb.toString();
	}
}
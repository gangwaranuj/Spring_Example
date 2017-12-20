package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class SaveMappingRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private FieldMappingGroup mappingGroup;

	public SaveMappingRequest() {
	}

	public SaveMappingRequest(String userNumber, FieldMappingGroup mappingGroup) {
		this();
		this.userNumber = userNumber;
		this.mappingGroup = mappingGroup;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public SaveMappingRequest setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public FieldMappingGroup getMappingGroup() {
		return this.mappingGroup;
	}

	public SaveMappingRequest setMappingGroup(FieldMappingGroup mappingGroup) {
		this.mappingGroup = mappingGroup;
		return this;
	}

	public boolean isSetMappingGroup() {
		return this.mappingGroup != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SaveMappingRequest)
			return this.equals((SaveMappingRequest) that);
		return false;
	}

	private boolean equals(SaveMappingRequest that) {
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

		boolean this_present_mappingGroup = true && this.isSetMappingGroup();
		boolean that_present_mappingGroup = true && that.isSetMappingGroup();
		if (this_present_mappingGroup || that_present_mappingGroup) {
			if (!(this_present_mappingGroup && that_present_mappingGroup))
				return false;
			if (!this.mappingGroup.equals(that.mappingGroup))
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

		boolean present_mappingGroup = true && (isSetMappingGroup());
		builder.append(present_mappingGroup);
		if (present_mappingGroup)
			builder.append(mappingGroup);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SaveMappingRequest(");
		boolean first = true;

		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("mappingGroup:");
		if (this.mappingGroup == null) {
			sb.append("null");
		} else {
			sb.append(this.mappingGroup);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
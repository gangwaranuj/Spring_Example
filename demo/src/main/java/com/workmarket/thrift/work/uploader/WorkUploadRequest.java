package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkUploadRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private String uploadUuid;
	private boolean headersProvided;
	private FieldMappingGroup mappingGroup;
	private long mappingGroupId;
	private long templateId;
	private long labelId;

	public WorkUploadRequest() {
	}

	public WorkUploadRequest(String userNumber, String uploadUuid, boolean headersProvided) {
		this();
		this.userNumber = userNumber;
		this.uploadUuid = uploadUuid;
		this.headersProvided = headersProvided;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public WorkUploadRequest setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public String getUploadUuid() {
		return this.uploadUuid;
	}

	public WorkUploadRequest setUploadUuid(String uploadUuid) {
		this.uploadUuid = uploadUuid;
		return this;
	}

	public boolean isSetUploadUuid() {
		return this.uploadUuid != null;
	}

	public boolean isHeadersProvided() {
		return this.headersProvided;
	}

	public WorkUploadRequest setHeadersProvided(boolean headersProvided) {
		this.headersProvided = headersProvided;
		return this;
	}

	public FieldMappingGroup getMappingGroup() {
		return this.mappingGroup;
	}

	public WorkUploadRequest setMappingGroup(FieldMappingGroup mappingGroup) {
		this.mappingGroup = mappingGroup;
		return this;
	}

	public boolean isSetMappingGroup() {
		return this.mappingGroup != null;
	}

	public long getMappingGroupId() {
		return this.mappingGroupId;
	}

	public WorkUploadRequest setMappingGroupId(long mappingGroupId) {
		this.mappingGroupId = mappingGroupId;
		return this;
	}

	public boolean isSetMappingGroupId() {
		return mappingGroupId > 0L;
	}

	public long getTemplateId() {
		return this.templateId;
	}

	public WorkUploadRequest setTemplateId(long templateId) {
		this.templateId = templateId;
		return this;
	}

	public boolean isSetTemplateId() {
		return templateId > 0L;
	}

	public long getLabelId() {
		return this.labelId;
	}

	public WorkUploadRequest setLabelId(long labelId) {
		this.labelId = labelId;
		return this;
	}

	public boolean isSetLabelId() {
		return labelId > 0L;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkUploadRequest)
			return this.equals((WorkUploadRequest) that);
		return false;
	}

	private boolean equals(WorkUploadRequest that) {
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

		boolean this_present_uploadUuid = true && this.isSetUploadUuid();
		boolean that_present_uploadUuid = true && that.isSetUploadUuid();
		if (this_present_uploadUuid || that_present_uploadUuid) {
			if (!(this_present_uploadUuid && that_present_uploadUuid))
				return false;
			if (!this.uploadUuid.equals(that.uploadUuid))
				return false;
		}

		boolean this_present_headersProvided = true;
		boolean that_present_headersProvided = true;
		if (this_present_headersProvided || that_present_headersProvided) {
			if (!(this_present_headersProvided && that_present_headersProvided))
				return false;
			if (this.headersProvided != that.headersProvided)
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

		boolean this_present_mappingGroupId = true && this.isSetMappingGroupId();
		boolean that_present_mappingGroupId = true && that.isSetMappingGroupId();
		if (this_present_mappingGroupId || that_present_mappingGroupId) {
			if (!(this_present_mappingGroupId && that_present_mappingGroupId))
				return false;
			if (this.mappingGroupId != that.mappingGroupId)
				return false;
		}

		boolean this_present_templateId = true && this.isSetTemplateId();
		boolean that_present_templateId = true && that.isSetTemplateId();
		if (this_present_templateId || that_present_templateId) {
			if (!(this_present_templateId && that_present_templateId))
				return false;
			if (this.templateId != that.templateId)
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

		boolean present_uploadUuid = true && (isSetUploadUuid());
		builder.append(present_uploadUuid);
		if (present_uploadUuid)
			builder.append(uploadUuid);

		boolean present_headersProvided = true;
		builder.append(present_headersProvided);
		if (present_headersProvided)
			builder.append(headersProvided);

		boolean present_mappingGroup = true && (isSetMappingGroup());
		builder.append(present_mappingGroup);
		if (present_mappingGroup)
			builder.append(mappingGroup);

		boolean present_mappingGroupId = true && (isSetMappingGroupId());
		builder.append(present_mappingGroupId);
		if (present_mappingGroupId)
			builder.append(mappingGroupId);

		boolean present_templateId = true && (isSetTemplateId());
		builder.append(present_templateId);
		if (present_templateId)
			builder.append(templateId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkUploadRequest(");
		boolean first = true;

		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("uploadUuid:");
		if (this.uploadUuid == null) {
			sb.append("null");
		} else {
			sb.append(this.uploadUuid);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("headersProvided:");
		sb.append(this.headersProvided);
		first = false;
		if (isSetMappingGroup()) {
			if (!first) sb.append(", ");
			sb.append("mappingGroup:");
			if (this.mappingGroup == null) {
				sb.append("null");
			} else {
				sb.append(this.mappingGroup);
			}
			first = false;
		}
		if (isSetMappingGroupId()) {
			if (!first) sb.append(", ");
			sb.append("mappingGroupId:");
			sb.append(this.mappingGroupId);
			first = false;
		}
		if (isSetTemplateId()) {
			if (!first) sb.append(", ");
			sb.append("templateId:");
			sb.append(this.templateId);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
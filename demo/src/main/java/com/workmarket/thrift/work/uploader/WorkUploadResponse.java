package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkUploadResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private FieldMappingGroup mappingGroup;
	private List<WorkUpload> uploads;
	private String json;
	private List<WorkUpload> errorUploads;
	private List<WorkUploadError> warnings;
	private int resourceCount;
	private int uploadCount;

	public WorkUploadResponse() {
	}

	public WorkUploadResponse(FieldMappingGroup mappingGroup, List<WorkUpload> uploads, String json) {
		this();
		this.mappingGroup = mappingGroup;
		this.uploads = uploads;
		this.json = json;
	}

	public FieldMappingGroup getMappingGroup() {
		return this.mappingGroup;
	}

	public WorkUploadResponse setMappingGroup(FieldMappingGroup mappingGroup) {
		this.mappingGroup = mappingGroup;
		return this;
	}

	/**
	 * Returns true if field mappingGroup is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetMappingGroup() {
		return this.mappingGroup != null;
	}

	public int getUploadsSize() {
		return (this.uploads == null) ? 0 : this.uploads.size();
	}

	public java.util.Iterator<WorkUpload> getUploadsIterator() {
		return (this.uploads == null) ? null : this.uploads.iterator();
	}

	public void addToUploads(WorkUpload elem) {
		if (this.uploads == null) {
			this.uploads = new ArrayList<WorkUpload>();
		}
		this.uploads.add(elem);
	}

	public List<WorkUpload> getUploads() {
		return this.uploads;
	}

	public WorkUploadResponse setUploads(List<WorkUpload> uploads) {
		this.uploads = uploads;
		return this;
	}

	/**
	 * Returns true if field uploads is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetUploads() {
		return this.uploads != null;
	}

	public String getJson() {
		return this.json;
	}

	public WorkUploadResponse setJson(String json) {
		this.json = json;
		return this;
	}

	/**
	 * Returns true if field json is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetJson() {
		return this.json != null;
	}

	public int getErrorUploadsSize() {
		return (this.errorUploads == null) ? 0 : this.errorUploads.size();
	}

	public java.util.Iterator<WorkUpload> getErrorUploadsIterator() {
		return (this.errorUploads == null) ? null : this.errorUploads.iterator();
	}

	public void addToErrorUploads(WorkUpload elem) {
		if (this.errorUploads == null) {
			this.errorUploads = new ArrayList<WorkUpload>();
		}
		this.errorUploads.add(elem);
	}

	public List<WorkUpload> getErrorUploads() {
		return this.errorUploads;
	}

	public WorkUploadResponse setErrorUploads(List<WorkUpload> errorUploads) {
		this.errorUploads = errorUploads;
		return this;
	}

	/**
	 * Returns true if field errorUploads is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetErrorUploads() {
		return this.errorUploads != null;
	}

	public int getWarningsSize() {
		return (this.warnings == null) ? 0 : this.warnings.size();
	}

	public java.util.Iterator<WorkUploadError> getWarningsIterator() {
		return (this.warnings == null) ? null : this.warnings.iterator();
	}

	public void addToWarnings(WorkUploadError elem) {
		if (this.warnings == null) {
			this.warnings = new ArrayList<WorkUploadError>();
		}
		this.warnings.add(elem);
	}

	public List<WorkUploadError> getWarnings() {
		return this.warnings;
	}

	public WorkUploadResponse setWarnings(List<WorkUploadError> warnings) {
		this.warnings = warnings;
		return this;
	}

	/**
	 * Returns true if field warnings is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetWarnings() {
		return this.warnings != null;
	}

	public int getResourceCount() {
		return this.resourceCount;
	}

	public WorkUploadResponse setResourceCount(int resourceCount) {
		this.resourceCount = resourceCount;
		return this;
	}

	public int getUploadCount() {
		return uploadCount;
	}

	public void setUploadCount(int uploadCount) {
		this.uploadCount = uploadCount;
	}

	/**
	 * Returns true if field resourceCount is set (has been assigned a value) and false otherwise
	 */
	public boolean isSetResourceCount() {
		return (resourceCount > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkUploadResponse)
			return this.equals((WorkUploadResponse) that);
		return false;
	}

	private boolean equals(WorkUploadResponse that) {
		if (that == null)
			return false;

		boolean this_present_mappingGroup = true && this.isSetMappingGroup();
		boolean that_present_mappingGroup = true && that.isSetMappingGroup();
		if (this_present_mappingGroup || that_present_mappingGroup) {
			if (!(this_present_mappingGroup && that_present_mappingGroup))
				return false;
			if (!this.mappingGroup.equals(that.mappingGroup))
				return false;
		}

		boolean this_present_uploads = true && this.isSetUploads();
		boolean that_present_uploads = true && that.isSetUploads();
		if (this_present_uploads || that_present_uploads) {
			if (!(this_present_uploads && that_present_uploads))
				return false;
			if (!this.uploads.equals(that.uploads))
				return false;
		}

		boolean this_present_json = true && this.isSetJson();
		boolean that_present_json = true && that.isSetJson();
		if (this_present_json || that_present_json) {
			if (!(this_present_json && that_present_json))
				return false;
			if (!this.json.equals(that.json))
				return false;
		}

		boolean this_present_errorUploads = true && this.isSetErrorUploads();
		boolean that_present_errorUploads = true && that.isSetErrorUploads();
		if (this_present_errorUploads || that_present_errorUploads) {
			if (!(this_present_errorUploads && that_present_errorUploads))
				return false;
			if (!this.errorUploads.equals(that.errorUploads))
				return false;
		}

		boolean this_present_warnings = true && this.isSetWarnings();
		boolean that_present_warnings = true && that.isSetWarnings();
		if (this_present_warnings || that_present_warnings) {
			if (!(this_present_warnings && that_present_warnings))
				return false;
			if (!this.warnings.equals(that.warnings))
				return false;
		}

		boolean this_present_resourceCount = true && this.isSetResourceCount();
		boolean that_present_resourceCount = true && that.isSetResourceCount();
		if (this_present_resourceCount || that_present_resourceCount) {
			if (!(this_present_resourceCount && that_present_resourceCount))
				return false;
			if (this.resourceCount != that.resourceCount)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_mappingGroup = true && (isSetMappingGroup());
		builder.append(present_mappingGroup);
		if (present_mappingGroup)
			builder.append(mappingGroup);

		boolean present_uploads = true && (isSetUploads());
		builder.append(present_uploads);
		if (present_uploads)
			builder.append(uploads);

		boolean present_json = true && (isSetJson());
		builder.append(present_json);
		if (present_json)
			builder.append(json);

		boolean present_errorUploads = true && (isSetErrorUploads());
		builder.append(present_errorUploads);
		if (present_errorUploads)
			builder.append(errorUploads);

		boolean present_warnings = true && (isSetWarnings());
		builder.append(present_warnings);
		if (present_warnings)
			builder.append(warnings);

		boolean present_resourceCount = true && (isSetResourceCount());
		builder.append(present_resourceCount);
		if (present_resourceCount)
			builder.append(resourceCount);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkUploadResponse(");
		boolean first = true;

		sb.append("mappingGroup:");
		if (this.mappingGroup == null) {
			sb.append("null");
		} else {
			sb.append(this.mappingGroup);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("uploads:");
		if (this.uploads == null) {
			sb.append("null");
		} else {
			sb.append(this.uploads);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("json:");
		if (this.json == null) {
			sb.append("null");
		} else {
			sb.append(this.json);
		}
		first = false;
		if (isSetErrorUploads()) {
			if (!first) sb.append(", ");
			sb.append("errorUploads:");
			if (this.errorUploads == null) {
				sb.append("null");
			} else {
				sb.append(this.errorUploads);
			}
			first = false;
		}
		if (isSetWarnings()) {
			if (!first) sb.append(", ");
			sb.append("warnings:");
			if (this.warnings == null) {
				sb.append("null");
			} else {
				sb.append(this.warnings);
			}
			first = false;
		}
		if (isSetResourceCount()) {
			if (!first) sb.append(", ");
			sb.append("resourceCount:");
			sb.append(this.resourceCount);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}

package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Asset implements Serializable, Comparable<Asset> {
	private static final long serialVersionUID = 1L;

	private long id;
	private String uuid;
	private String transformLargeUuid;
	private String transformSmallUuid;
	private String name;
	private String description;
	private String mimeType;
	private String type;
	private String uri;
	private String visibilityCode;
	private long createdOn;

	public Asset() {}

	public Asset(
			long id,
			String uuid,
			String name,
			String description,
			String mimeType,
			String type,
			String uri) {
		this();
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.mimeType = mimeType;
		this.type = type;
		this.uri = uri;
	}

	public long getId() {
		return this.id;
	}

	public Asset setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getUuid() {
		return this.uuid;
	}

	public Asset setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public boolean isSetUuid() {
		return this.uuid != null;
	}

	public String getTransformLargeUuid() {
		return transformLargeUuid;
	}

	public void setTransformLargeUuid(String transformLargeUuid) {
		this.transformLargeUuid = transformLargeUuid;
	}

	public boolean isSetTransformLargeUuid() {
		return this.transformLargeUuid != null;
	}

	public String getTransformSmallUuid() {
		return transformSmallUuid;
	}

	public void setTransformSmallUuid(String transformSmallUuid) {
		this.transformSmallUuid = transformSmallUuid;
	}

	public boolean isSetTransformSmallUuid() {
		return this.transformSmallUuid != null;
	}

	public String getName() {
		return this.name;
	}

	public Asset setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public String getDescription() {
		return this.description;
	}

	public Asset setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public Asset setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public boolean isSetMimeType() {
		return this.mimeType != null;
	}

	public String getType() {
		return this.type;
	}

	public Asset setType(String type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public String getUri() {
		return this.uri;
	}

	public Asset setUri(String uri) {
		this.uri = uri;
		return this;
	}

	public boolean isSetUri() {
		return this.uri != null;
	}

	public String getVisibilityCode() {
		return visibilityCode;
	}

	public Asset setVisibilityCode(String visibilityCode) {
		this.visibilityCode = visibilityCode;
		return this;
	}

	public boolean isSetVisibility() {
		return this.visibilityCode != null;
	}

	@Override
	public boolean equals(Object that) {
		return that != null && that instanceof Asset && this.equals((Asset) that);
	}

	private boolean equals(Asset that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_uuid = true && this.isSetUuid();
		boolean that_present_uuid = true && that.isSetUuid();
		if (this_present_uuid || that_present_uuid) {
			if (!(this_present_uuid && that_present_uuid))
				return false;
			if (!this.uuid.equals(that.uuid))
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

		boolean this_present_description = true && this.isSetDescription();
		boolean that_present_description = true && that.isSetDescription();
		if (this_present_description || that_present_description) {
			if (!(this_present_description && that_present_description))
				return false;
			if (!this.description.equals(that.description))
				return false;
		}

		boolean this_present_mimeType = true && this.isSetMimeType();
		boolean that_present_mimeType = true && that.isSetMimeType();
		if (this_present_mimeType || that_present_mimeType) {
			if (!(this_present_mimeType && that_present_mimeType))
				return false;
			if (!this.mimeType.equals(that.mimeType))
				return false;
		}

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		boolean this_present_uri = true && this.isSetUri();
		boolean that_present_uri = true && that.isSetUri();
		if (this_present_uri || that_present_uri) {
			if (!(this_present_uri && that_present_uri))
				return false;
			if (!this.uri.equals(that.uri))
				return false;
		}

		boolean this_present_visibility = this.isSetVisibility();
		boolean that_present_visibility = that.isSetVisibility();
		if (this_present_visibility || that_present_visibility) {
			if (!(this_present_visibility && that_present_visibility)) {
				return false;
			}
			if (!this.visibilityCode.equals(that.visibilityCode)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_uuid = true && (isSetUuid());
		builder.append(present_uuid);
		if (present_uuid)
			builder.append(uuid);

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		boolean present_mimeType = true && (isSetMimeType());
		builder.append(present_mimeType);
		if (present_mimeType)
			builder.append(mimeType);

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type);

		boolean present_uri = true && (isSetUri());
		builder.append(present_uri);
		if (present_uri)
			builder.append(uri);

		boolean present_visibilityType = isSetVisibility();
		builder.append(present_visibilityType);
		if (present_visibilityType) {
			builder.append(visibilityCode);
		}

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Asset(");
		boolean first;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("uuid:");
		if (this.uuid == null) {
			sb.append("null");
		} else {
			sb.append(this.uuid);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("description:");
		if (this.description == null) {
			sb.append("null");
		} else {
			sb.append(this.description);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("mimeType:");
		if (this.mimeType == null) {
			sb.append("null");
		} else {
			sb.append(this.mimeType);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("uri:");
		if (this.uri == null) {
			sb.append("null");
		} else {
			sb.append(this.uri);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("visibility:");
		if (this.visibilityCode == null) {
			sb.append("null");
		} else {
			sb.append(this.visibilityCode);
		}

		sb.append(")");
		return sb.toString();
	}

	public String getThumbnailUuid() {
		if(StringUtils.isNotBlank(transformLargeUuid)) {
			return transformLargeUuid;
		}
		return uuid;
	}

	@Override
	public int compareTo(Asset asset) {
		if (asset == null)
			return 1;
		return Long.compare(this.getId(), asset.getId());
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public Asset setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}
}


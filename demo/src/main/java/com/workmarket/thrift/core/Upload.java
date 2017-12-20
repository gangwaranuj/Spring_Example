package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Upload implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String uuid;
	private String name;
	private String description;
	private String uri;
	private String visibilityCode;

	public Upload() {}

	public Upload(long id, String uuid, String name, String description, String uri) {
		this();
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.uri = uri;
	}

	public long getId() {
		return this.id;
	}

	public Upload setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getUuid() {
		return this.uuid;
	}

	public Upload setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public boolean isSetUuid() {
		return this.uuid != null;
	}

	public String getName() {
		return this.name;
	}

	public Upload setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public String getDescription() {
		return this.description;
	}

	public Upload setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public String getUri() {
		return this.uri;
	}

	public Upload setUri(String uri) {
		this.uri = uri;
		return this;
	}

	public boolean isSetUri() {
		return this.uri != null;
	}

	public String getVisibilityCode() {
		return visibilityCode;
	}

	public Upload setVisibilityCode(String visibilityCode) {
		this.visibilityCode = visibilityCode;
		return this;
	}

	public boolean isSetVisibility() {
		return this.visibilityCode != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Upload)
			return this.equals((Upload) that);
		return false;
	}

	private boolean equals(Upload that) {
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
		StringBuilder sb = new StringBuilder("Upload(");
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
}


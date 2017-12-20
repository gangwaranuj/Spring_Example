package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FieldMappingGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private List<FieldMapping> mappings;

	public FieldMappingGroup() {
	}

	public FieldMappingGroup(long id, String name, List<FieldMapping> mappings) {
		this();
		this.id = id;
		this.name = name;
		this.mappings = mappings;
	}

	public long getId() {
		return this.id;
	}

	public FieldMappingGroup setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getName() {
		return this.name;
	}

	public FieldMappingGroup setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public int getMappingsSize() {
		return (this.mappings == null) ? 0 : this.mappings.size();
	}

	public java.util.Iterator<FieldMapping> getMappingsIterator() {
		return (this.mappings == null) ? null : this.mappings.iterator();
	}

	public void addToMappings(FieldMapping elem) {
		if (this.mappings == null) {
			this.mappings = new ArrayList<FieldMapping>();
		}
		this.mappings.add(elem);
	}

	public List<FieldMapping> getMappings() {
		return this.mappings;
	}

	public FieldMappingGroup setMappings(List<FieldMapping> mappings) {
		this.mappings = mappings;
		return this;
	}

	public boolean isSetMappings() {
		return this.mappings != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FieldMappingGroup)
			return this.equals((FieldMappingGroup) that);
		return false;
	}

	private boolean equals(FieldMappingGroup that) {
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

		boolean this_present_name = true && this.isSetName();
		boolean that_present_name = true && that.isSetName();
		if (this_present_name || that_present_name) {
			if (!(this_present_name && that_present_name))
				return false;
			if (!this.name.equals(that.name))
				return false;
		}

		boolean this_present_mappings = true && this.isSetMappings();
		boolean that_present_mappings = true && that.isSetMappings();
		if (this_present_mappings || that_present_mappings) {
			if (!(this_present_mappings && that_present_mappings))
				return false;
			if (!this.mappings.equals(that.mappings))
				return false;
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

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		boolean present_mappings = true && (isSetMappings());
		builder.append(present_mappings);
		if (present_mappings)
			builder.append(mappings);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FieldMappingGroup(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
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
		sb.append("mappings:");
		if (this.mappings == null) {
			sb.append("null");
		} else {
			sb.append(this.mappings);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
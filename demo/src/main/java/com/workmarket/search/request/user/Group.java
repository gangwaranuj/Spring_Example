package com.workmarket.search.request.user;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String groupName;
	private String groupDescription;

	public Group() {
	}

	public Group(long id, String groupName, String groupDescription) {
		this();
		this.id = id;
		this.groupName = groupName;
		this.groupDescription = groupDescription;
	}

	public long getId() {
		return this.id;
	}

	public Group setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getGroupName() {
		return this.groupName;
	}

	public Group setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public boolean isSetGroupName() {
		return this.groupName != null;
	}

	public String getGroupDescription() {
		return this.groupDescription;
	}

	public Group setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
		return this;
	}

	public boolean isSetGroupDescription() {
		return this.groupDescription != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Group)
			return this.equals((Group) that);
		return false;
	}

	private boolean equals(Group that) {
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

		boolean this_present_groupName = true && this.isSetGroupName();
		boolean that_present_groupName = true && that.isSetGroupName();
		if (this_present_groupName || that_present_groupName) {
			if (!(this_present_groupName && that_present_groupName))
				return false;
			if (!this.groupName.equals(that.groupName))
				return false;
		}

		boolean this_present_groupDescription = true && this.isSetGroupDescription();
		boolean that_present_groupDescription = true && that.isSetGroupDescription();
		if (this_present_groupDescription || that_present_groupDescription) {
			if (!(this_present_groupDescription && that_present_groupDescription))
				return false;
			if (!this.groupDescription.equals(that.groupDescription))
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

		boolean present_groupName = true && (isSetGroupName());
		builder.append(present_groupName);
		if (present_groupName)
			builder.append(groupName);

		boolean present_groupDescription = true && (isSetGroupDescription());
		builder.append(present_groupDescription);
		if (present_groupDescription)
			builder.append(groupDescription);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Group(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("groupName:");
		if (this.groupName == null) {
			sb.append("null");
		} else {
			sb.append(this.groupName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("groupDescription:");
		if (this.groupDescription == null) {
			sb.append("null");
		} else {
			sb.append(this.groupDescription);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


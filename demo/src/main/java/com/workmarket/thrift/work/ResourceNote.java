package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class ResourceNote implements Serializable {
	private static final long serialVersionUID = 1L;

	private ResourceNoteType hoverType;
	private String note;
	private String actionCodeDescription;
	private long dateOfNote;
	private long resourceId;
	private String onBehalfOfUserName;
	private String masqueradeUserName;
	private long actionCodeId;
	private com.workmarket.thrift.core.User onBehalfOfUser;
	private com.workmarket.thrift.core.User masqueradeUser;
	private com.workmarket.thrift.core.User resourceUser;

	public ResourceNote() {
	}

	public ResourceNote(ResourceNoteType hoverType) {
		this();
		this.hoverType = hoverType;
	}

	public ResourceNoteType getHoverType() {
		return this.hoverType;
	}

	public ResourceNote setHoverType(ResourceNoteType hoverType) {
		this.hoverType = hoverType;
		return this;
	}

	public boolean isSetHoverType() {
		return this.hoverType != null;
	}

	public String getNote() {
		return this.note;
	}

	public ResourceNote setNote(String note) {
		this.note = note;
		return this;
	}

	public boolean isSetNote() {
		return this.note != null;
	}

	public String getActionCodeDescription() {
		return this.actionCodeDescription;
	}

	public ResourceNote setActionCodeDescription(String actionCodeDescription) {
		this.actionCodeDescription = actionCodeDescription;
		return this;
	}

	public boolean isSetActionCodeDescription() {
		return this.actionCodeDescription != null;
	}

	public long getDateOfNote() {
		return this.dateOfNote;
	}

	public ResourceNote setDateOfNote(long dateOfNote) {
		this.dateOfNote = dateOfNote;
		return this;
	}

	public boolean isSetDateOfNote() {
		return (dateOfNote > 0L);
	}

	public long getResourceId() {
		return this.resourceId;
	}

	public ResourceNote setResourceId(long resourceId) {
		this.resourceId = resourceId;
		return this;
	}

	public boolean isSetResourceId() {
		return (resourceId > 0L);
	}

	public String getOnBehalfOfUserName() {
		return this.onBehalfOfUserName;
	}

	public ResourceNote setOnBehalfOfUserName(String onBehalfOfUserName) {
		this.onBehalfOfUserName = onBehalfOfUserName;
		return this;
	}

	public boolean isSetOnBehalfOfUserName() {
		return this.onBehalfOfUserName != null;
	}

	public String getMasqueradeUserName() {
		return this.masqueradeUserName;
	}

	public ResourceNote setMasqueradeUserName(String masqueradeUserName) {
		this.masqueradeUserName = masqueradeUserName;
		return this;
	}

	public boolean isSetMasqueradeUserName() {
		return this.masqueradeUserName != null;
	}

	public long getActionCodeId() {
		return this.actionCodeId;
	}

	public ResourceNote setActionCodeId(long actionCodeId) {
		this.actionCodeId = actionCodeId;
		return this;
	}

	public boolean isSetActionCodeId() {
		return (actionCodeId > 0L);
	}

	public com.workmarket.thrift.core.User getOnBehalfOfUser() {
		return this.onBehalfOfUser;
	}

	public ResourceNote setOnBehalfOfUser(com.workmarket.thrift.core.User onBehalfOfUser) {
		this.onBehalfOfUser = onBehalfOfUser;
		return this;
	}

	public boolean isSetOnBehalfOfUser() {
		return this.onBehalfOfUser != null;
	}

	public com.workmarket.thrift.core.User getMasqueradeUser() {
		return this.masqueradeUser;
	}

	public ResourceNote setMasqueradeUser(com.workmarket.thrift.core.User masqueradeUser) {
		this.masqueradeUser = masqueradeUser;
		return this;
	}

	public boolean isSetMasqueradeUser() {
		return this.masqueradeUser != null;
	}

	public com.workmarket.thrift.core.User getResourceUser() {
		return this.resourceUser;
	}

	public ResourceNote setResourceUser(com.workmarket.thrift.core.User resourceUser) {
		this.resourceUser = resourceUser;
		return this;
	}

	public boolean isSetResourceUser() {
		return this.resourceUser != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ResourceNote)
			return this.equals((ResourceNote) that);
		return false;
	}

	private boolean equals(ResourceNote that) {
		if (that == null)
			return false;

		boolean this_present_hoverType = true && this.isSetHoverType();
		boolean that_present_hoverType = true && that.isSetHoverType();
		if (this_present_hoverType || that_present_hoverType) {
			if (!(this_present_hoverType && that_present_hoverType))
				return false;
			if (!this.hoverType.equals(that.hoverType))
				return false;
		}

		boolean this_present_note = true && this.isSetNote();
		boolean that_present_note = true && that.isSetNote();
		if (this_present_note || that_present_note) {
			if (!(this_present_note && that_present_note))
				return false;
			if (!this.note.equals(that.note))
				return false;
		}

		boolean this_present_actionCodeDescription = true && this.isSetActionCodeDescription();
		boolean that_present_actionCodeDescription = true && that.isSetActionCodeDescription();
		if (this_present_actionCodeDescription || that_present_actionCodeDescription) {
			if (!(this_present_actionCodeDescription && that_present_actionCodeDescription))
				return false;
			if (!this.actionCodeDescription.equals(that.actionCodeDescription))
				return false;
		}

		boolean this_present_dateOfNote = true && this.isSetDateOfNote();
		boolean that_present_dateOfNote = true && that.isSetDateOfNote();
		if (this_present_dateOfNote || that_present_dateOfNote) {
			if (!(this_present_dateOfNote && that_present_dateOfNote))
				return false;
			if (this.dateOfNote != that.dateOfNote)
				return false;
		}

		boolean this_present_resourceId = true && this.isSetResourceId();
		boolean that_present_resourceId = true && that.isSetResourceId();
		if (this_present_resourceId || that_present_resourceId) {
			if (!(this_present_resourceId && that_present_resourceId))
				return false;
			if (this.resourceId != that.resourceId)
				return false;
		}

		boolean this_present_onBehalfOfUserName = true && this.isSetOnBehalfOfUserName();
		boolean that_present_onBehalfOfUserName = true && that.isSetOnBehalfOfUserName();
		if (this_present_onBehalfOfUserName || that_present_onBehalfOfUserName) {
			if (!(this_present_onBehalfOfUserName && that_present_onBehalfOfUserName))
				return false;
			if (!this.onBehalfOfUserName.equals(that.onBehalfOfUserName))
				return false;
		}

		boolean this_present_masqueradeUserName = true && this.isSetMasqueradeUserName();
		boolean that_present_masqueradeUserName = true && that.isSetMasqueradeUserName();
		if (this_present_masqueradeUserName || that_present_masqueradeUserName) {
			if (!(this_present_masqueradeUserName && that_present_masqueradeUserName))
				return false;
			if (!this.masqueradeUserName.equals(that.masqueradeUserName))
				return false;
		}

		boolean this_present_actionCodeId = true && this.isSetActionCodeId();
		boolean that_present_actionCodeId = true && that.isSetActionCodeId();
		if (this_present_actionCodeId || that_present_actionCodeId) {
			if (!(this_present_actionCodeId && that_present_actionCodeId))
				return false;
			if (this.actionCodeId != that.actionCodeId)
				return false;
		}

		boolean this_present_onBehalfOfUser = true && this.isSetOnBehalfOfUser();
		boolean that_present_onBehalfOfUser = true && that.isSetOnBehalfOfUser();
		if (this_present_onBehalfOfUser || that_present_onBehalfOfUser) {
			if (!(this_present_onBehalfOfUser && that_present_onBehalfOfUser))
				return false;
			if (!this.onBehalfOfUser.equals(that.onBehalfOfUser))
				return false;
		}

		boolean this_present_masqueradeUser = true && this.isSetMasqueradeUser();
		boolean that_present_masqueradeUser = true && that.isSetMasqueradeUser();
		if (this_present_masqueradeUser || that_present_masqueradeUser) {
			if (!(this_present_masqueradeUser && that_present_masqueradeUser))
				return false;
			if (!this.masqueradeUser.equals(that.masqueradeUser))
				return false;
		}

		boolean this_present_resourceUser = true && this.isSetResourceUser();
		boolean that_present_resourceUser = true && that.isSetResourceUser();
		if (this_present_resourceUser || that_present_resourceUser) {
			if (!(this_present_resourceUser && that_present_resourceUser))
				return false;
			if (!this.resourceUser.equals(that.resourceUser))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_hoverType = true && (isSetHoverType());
		builder.append(present_hoverType);
		if (present_hoverType)
			builder.append(hoverType.getValue());

		boolean present_note = true && (isSetNote());
		builder.append(present_note);
		if (present_note)
			builder.append(note);

		boolean present_actionCodeDescription = true && (isSetActionCodeDescription());
		builder.append(present_actionCodeDescription);
		if (present_actionCodeDescription)
			builder.append(actionCodeDescription);

		boolean present_dateOfNote = true && (isSetDateOfNote());
		builder.append(present_dateOfNote);
		if (present_dateOfNote)
			builder.append(dateOfNote);

		boolean present_resourceId = true && (isSetResourceId());
		builder.append(present_resourceId);
		if (present_resourceId)
			builder.append(resourceId);

		boolean present_onBehalfOfUserName = true && (isSetOnBehalfOfUserName());
		builder.append(present_onBehalfOfUserName);
		if (present_onBehalfOfUserName)
			builder.append(onBehalfOfUserName);

		boolean present_masqueradeUserName = true && (isSetMasqueradeUserName());
		builder.append(present_masqueradeUserName);
		if (present_masqueradeUserName)
			builder.append(masqueradeUserName);

		boolean present_actionCodeId = true && (isSetActionCodeId());
		builder.append(present_actionCodeId);
		if (present_actionCodeId)
			builder.append(actionCodeId);

		boolean present_onBehalfOfUser = true && (isSetOnBehalfOfUser());
		builder.append(present_onBehalfOfUser);
		if (present_onBehalfOfUser)
			builder.append(onBehalfOfUser);

		boolean present_masqueradeUser = true && (isSetMasqueradeUser());
		builder.append(present_masqueradeUser);
		if (present_masqueradeUser)
			builder.append(masqueradeUser);

		boolean present_resourceUser = true && (isSetResourceUser());
		builder.append(present_resourceUser);
		if (present_resourceUser)
			builder.append(resourceUser);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ResourceNote(");
		boolean first = true;

		sb.append("hoverType:");
		if (this.hoverType == null) {
			sb.append("null");
		} else {
			sb.append(this.hoverType);
		}
		first = false;
		if (isSetNote()) {
			if (!first) sb.append(", ");
			sb.append("note:");
			if (this.note == null) {
				sb.append("null");
			} else {
				sb.append(this.note);
			}
			first = false;
		}
		if (isSetActionCodeDescription()) {
			if (!first) sb.append(", ");
			sb.append("actionCodeDescription:");
			if (this.actionCodeDescription == null) {
				sb.append("null");
			} else {
				sb.append(this.actionCodeDescription);
			}
			first = false;
		}
		if (isSetDateOfNote()) {
			if (!first) sb.append(", ");
			sb.append("dateOfNote:");
			sb.append(this.dateOfNote);
			first = false;
		}
		if (isSetResourceId()) {
			if (!first) sb.append(", ");
			sb.append("resourceId:");
			sb.append(this.resourceId);
			first = false;
		}
		if (isSetOnBehalfOfUserName()) {
			if (!first) sb.append(", ");
			sb.append("onBehalfOfUserName:");
			if (this.onBehalfOfUserName == null) {
				sb.append("null");
			} else {
				sb.append(this.onBehalfOfUserName);
			}
			first = false;
		}
		if (isSetMasqueradeUserName()) {
			if (!first) sb.append(", ");
			sb.append("masqueradeUserName:");
			if (this.masqueradeUserName == null) {
				sb.append("null");
			} else {
				sb.append(this.masqueradeUserName);
			}
			first = false;
		}
		if (isSetActionCodeId()) {
			if (!first) sb.append(", ");
			sb.append("actionCodeId:");
			sb.append(this.actionCodeId);
			first = false;
		}
		if (isSetOnBehalfOfUser()) {
			if (!first) sb.append(", ");
			sb.append("onBehalfOfUser:");
			if (this.onBehalfOfUser == null) {
				sb.append("null");
			} else {
				sb.append(this.onBehalfOfUser);
			}
			first = false;
		}
		if (isSetMasqueradeUser()) {
			if (!first) sb.append(", ");
			sb.append("masqueradeUser:");
			if (this.masqueradeUser == null) {
				sb.append("null");
			} else {
				sb.append(this.masqueradeUser);
			}
			first = false;
		}
		if (isSetResourceUser()) {
			if (!first) sb.append(", ");
			sb.append("resourceUser:");
			if (this.resourceUser == null) {
				sb.append("null");
			} else {
				sb.append(this.resourceUser);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
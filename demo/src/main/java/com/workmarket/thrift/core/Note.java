package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Note implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String text;
	private boolean isPrivate;
	private boolean isPrivileged;
	private User creator;
	private long createdOn;
	private User onBehalfOf;
	private String actionCodeText;
	private short actionCodeId;

	private String replyToName;

	public Note() {
	}

	public Note(long id, String text, boolean isPrivate, User creator, long createdOn) {
		this();
		this.id = id;
		this.text = text;
		this.isPrivate = isPrivate;
		this.creator = creator;
		this.createdOn = createdOn;
	}

	public long getId() {
		return this.id;
	}

	public Note setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getText() {
		return this.text;
	}

	public Note setText(String text) {
		this.text = text;
		return this;
	}

	public boolean isSetText() {
		return this.text != null;
	}

	public boolean isIsPrivate() {
		return this.isPrivate;
	}

	public boolean isIsPrivileged(){
		return this.isPrivileged;
	}

	public Note setIsPrivileged(boolean isPrivileged){
		this.isPrivileged = isPrivileged;
		return this;
	}

	public Note setIsPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
		return this;
	}

	public User getCreator() {
		return this.creator;
	}

	public Note setCreator(User creator) {
		this.creator = creator;
		return this;
	}

	public boolean isSetCreator() {
		return this.creator != null;
	}

	public long getCreatedOn() {
		return this.createdOn;
	}

	public Note setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public User getOnBehalfOf() {
		return this.onBehalfOf;
	}

	public Note setOnBehalfOf(User onBehalfOf) {
		this.onBehalfOf = onBehalfOf;
		return this;
	}

	public boolean isSetOnBehalfOf() {
		return this.onBehalfOf != null;
	}

	public String getReplyToName() {
		return replyToName;
	}

	public Note setReplyToName(String replyToName) {
		this.replyToName = replyToName;
		return this;
	}

	public String getActionCodeText() {
		return this.actionCodeText;
	}

	public Note setActionCodeText(String actionCodeText) {
		this.actionCodeText = actionCodeText;
		return this;
	}

	public boolean isSetActionCodeText() {
		return this.actionCodeText != null;
	}

	public short getActionCodeId() {
		return this.actionCodeId;
	}

	public Note setActionCodeId(short actionCodeId) {
		this.actionCodeId = actionCodeId;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Note)
			return this.equals((Note) that);
		return false;
	}

	private boolean equals(Note that) {
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

		boolean this_present_text = true && this.isSetText();
		boolean that_present_text = true && that.isSetText();
		if (this_present_text || that_present_text) {
			if (!(this_present_text && that_present_text))
				return false;
			if (!this.text.equals(that.text))
				return false;
		}

		boolean this_present_isPrivate = true;
		boolean that_present_isPrivate = true;
		if (this_present_isPrivate || that_present_isPrivate) {
			if (!(this_present_isPrivate && that_present_isPrivate))
				return false;
			if (this.isPrivate != that.isPrivate)
				return false;
		}

		boolean this_present_creator = true && this.isSetCreator();
		boolean that_present_creator = true && that.isSetCreator();
		if (this_present_creator || that_present_creator) {
			if (!(this_present_creator && that_present_creator))
				return false;
			if (!this.creator.equals(that.creator))
				return false;
		}

		boolean this_present_createdOn = true;
		boolean that_present_createdOn = true;
		if (this_present_createdOn || that_present_createdOn) {
			if (!(this_present_createdOn && that_present_createdOn))
				return false;
			if (this.createdOn != that.createdOn)
				return false;
		}

		boolean this_present_onBehalfOf = true && this.isSetOnBehalfOf();
		boolean that_present_onBehalfOf = true && that.isSetOnBehalfOf();
		if (this_present_onBehalfOf || that_present_onBehalfOf) {
			if (!(this_present_onBehalfOf && that_present_onBehalfOf))
				return false;
			if (!this.onBehalfOf.equals(that.onBehalfOf))
				return false;
		}

		boolean this_present_actionCodeText = true && this.isSetActionCodeText();
		boolean that_present_actionCodeText = true && that.isSetActionCodeText();
		if (this_present_actionCodeText || that_present_actionCodeText) {
			if (!(this_present_actionCodeText && that_present_actionCodeText))
				return false;
			if (!this.actionCodeText.equals(that.actionCodeText))
				return false;
		}

		boolean this_present_actionCodeId = true && (this.actionCodeId > 0);
		boolean that_present_actionCodeId = true && (that.actionCodeId > 0);
		if (this_present_actionCodeId || that_present_actionCodeId) {
			if (!(this_present_actionCodeId && that_present_actionCodeId))
				return false;
			if (this.actionCodeId != that.actionCodeId)
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

		boolean present_text = true && (isSetText());
		builder.append(present_text);
		if (present_text)
			builder.append(text);

		boolean present_isPrivate = true;
		builder.append(present_isPrivate);
		if (present_isPrivate)
			builder.append(isPrivate);

		boolean present_creator = true && (isSetCreator());
		builder.append(present_creator);
		if (present_creator)
			builder.append(creator);

		boolean present_createdOn = true;
		builder.append(present_createdOn);
		if (present_createdOn)
			builder.append(createdOn);

		boolean present_onBehalfOf = true && (isSetOnBehalfOf());
		builder.append(present_onBehalfOf);
		if (present_onBehalfOf)
			builder.append(onBehalfOf);

		boolean present_actionCodeText = true && (isSetActionCodeText());
		builder.append(present_actionCodeText);
		if (present_actionCodeText)
			builder.append(actionCodeText);

		boolean present_actionCodeId = true && (actionCodeId > 0);
		builder.append(present_actionCodeId);
		if (present_actionCodeId)
			builder.append(actionCodeId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Note(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("text:");
		if (this.text == null) {
			sb.append("null");
		} else {
			sb.append(this.text);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("isPrivate:");
		sb.append(this.isPrivate);
		first = false;
		if (!first) sb.append(", ");
		sb.append("creator:");
		if (this.creator == null) {
			sb.append("null");
		} else {
			sb.append(this.creator);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("createdOn:");
		sb.append(this.createdOn);
		first = false;
		if (isSetOnBehalfOf()) {
			if (!first) sb.append(", ");
			sb.append("onBehalfOf:");
			if (this.onBehalfOf == null) {
				sb.append("null");
			} else {
				sb.append(this.onBehalfOf);
			}
			first = false;
		}
		if (isSetActionCodeText()) {
			if (!first) sb.append(", ");
			sb.append("actionCodeText:");
			if (this.actionCodeText == null) {
				sb.append("null");
			} else {
				sb.append(this.actionCodeText);
			}
			first = false;
		}

		if (!first) sb.append(", ");
		sb.append("actionCodeId:");
		sb.append(this.actionCodeId);
		first = false;

		sb.append(")");
		return sb.toString();
	}
}


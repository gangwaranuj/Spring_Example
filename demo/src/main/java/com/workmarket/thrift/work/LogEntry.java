package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class LogEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private long timestamp;
	private String text;
	private String onBehalfOfUser;
	private com.workmarket.thrift.core.User actor;
	private LogEntryType type;
	private SubStatus subStatus;
	private SubStatusActionType subStatusActionType;
	private String status;
	private boolean isScheduleNegotiationOnly = false;
	private boolean isRejectAction = false;

	public LogEntry() {
	}

	public LogEntry(
			long timestamp,
			String text,
			String onBehalfOfUser,
			com.workmarket.thrift.core.User actor,
			LogEntryType type,
			SubStatus subStatus,
			SubStatusActionType subStatusActionType) {
		this();
		this.timestamp = timestamp;
		this.text = text;
		this.onBehalfOfUser = onBehalfOfUser;
		this.actor = actor;
		this.type = type;
		this.subStatus = subStatus;
		this.subStatusActionType = subStatusActionType;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public LogEntry setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public boolean isSetTimestamp() {
		return (timestamp > 0L);
	}

	public String getText() {
		return this.text;
	}

	public LogEntry setText(String text) {
		this.text = text;
		return this;
	}

	public boolean isSetText() {
		return this.text != null;
	}

	public String getOnBehalfOfUser() {
		return this.onBehalfOfUser;
	}

	public LogEntry setOnBehalfOfUser(String onBehalfOfUser) {
		this.onBehalfOfUser = onBehalfOfUser;
		return this;
	}

	public boolean isSetOnBehalfOfUser() {
		return this.onBehalfOfUser != null;
	}

	public com.workmarket.thrift.core.User getActor() {
		return this.actor;
	}

	public LogEntry setActor(com.workmarket.thrift.core.User actor) {
		this.actor = actor;
		return this;
	}

	public boolean isSetActor() {
		return this.actor != null;
	}

	public LogEntryType getType() {
		return this.type;
	}

	public LogEntry setType(LogEntryType type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public SubStatus getSubStatus() {
		return this.subStatus;
	}

	public LogEntry setSubStatus(SubStatus subStatus) {
		this.subStatus = subStatus;
		return this;
	}

	public boolean isSetSubStatus() {
		return this.subStatus != null;
	}

	public SubStatusActionType getSubStatusActionType() {
		return this.subStatusActionType;
	}

	public LogEntry setSubStatusActionType(SubStatusActionType subStatusActionType) {
		this.subStatusActionType = subStatusActionType;
		return this;
	}

	public boolean isSetSubStatusActionType() {
		return this.subStatusActionType != null;
	}

	public String getStatus() {
		return status;
	}

	public LogEntry setStatus(String status) {
		this.status = status;
		return this;
	}

	public boolean isScheduleNegotiationOnly() {
		return isScheduleNegotiationOnly;
	}

	public LogEntry setScheduleNegotiationOnly(boolean isScheduleNegotiationOnly) {
		this.isScheduleNegotiationOnly = isScheduleNegotiationOnly;
		return this;
	}

	public boolean isRejectAction() {
		return isRejectAction;
	}

	public LogEntry setRejectAction(boolean isRejectAction) {
		this.isRejectAction = isRejectAction;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof LogEntry)
			return this.equals((LogEntry) that);
		return false;
	}

	private boolean equals(LogEntry that) {
		if (that == null)
			return false;

		boolean this_present_timestamp = true;
		boolean that_present_timestamp = true;
		if (this_present_timestamp || that_present_timestamp) {
			if (!(this_present_timestamp && that_present_timestamp))
				return false;
			if (this.timestamp != that.timestamp)
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

		boolean this_present_onBehalfOfUser = true && this.isSetOnBehalfOfUser();
		boolean that_present_onBehalfOfUser = true && that.isSetOnBehalfOfUser();
		if (this_present_onBehalfOfUser || that_present_onBehalfOfUser) {
			if (!(this_present_onBehalfOfUser && that_present_onBehalfOfUser))
				return false;
			if (!this.onBehalfOfUser.equals(that.onBehalfOfUser))
				return false;
		}

		boolean this_present_actor = true && this.isSetActor();
		boolean that_present_actor = true && that.isSetActor();
		if (this_present_actor || that_present_actor) {
			if (!(this_present_actor && that_present_actor))
				return false;
			if (!this.actor.equals(that.actor))
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

		boolean this_present_subStatus = true && this.isSetSubStatus();
		boolean that_present_subStatus = true && that.isSetSubStatus();
		if (this_present_subStatus || that_present_subStatus) {
			if (!(this_present_subStatus && that_present_subStatus))
				return false;
			if (!this.subStatus.equals(that.subStatus))
				return false;
		}

		boolean this_present_subStatusActionType = true && this.isSetSubStatusActionType();
		boolean that_present_subStatusActionType = true && that.isSetSubStatusActionType();
		if (this_present_subStatusActionType || that_present_subStatusActionType) {
			if (!(this_present_subStatusActionType && that_present_subStatusActionType))
				return false;
			if (!this.subStatusActionType.equals(that.subStatusActionType))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_timestamp = true;
		builder.append(present_timestamp);
		if (present_timestamp)
			builder.append(timestamp);

		boolean present_text = true && (isSetText());
		builder.append(present_text);
		if (present_text)
			builder.append(text);

		boolean present_onBehalfOfUser = true && (isSetOnBehalfOfUser());
		builder.append(present_onBehalfOfUser);
		if (present_onBehalfOfUser)
			builder.append(onBehalfOfUser);

		boolean present_actor = true && (isSetActor());
		builder.append(present_actor);
		if (present_actor)
			builder.append(actor);

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type.getValue());

		boolean present_subStatus = true && (isSetSubStatus());
		builder.append(present_subStatus);
		if (present_subStatus)
			builder.append(subStatus);

		boolean present_subStatusActionType = true && (isSetSubStatusActionType());
		builder.append(present_subStatusActionType);
		if (present_subStatusActionType)
			builder.append(subStatusActionType.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("LogEntry(");
		boolean first = true;

		sb.append("timestamp:");
		sb.append(this.timestamp);
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
		sb.append("onBehalfOfUser:");
		if (this.onBehalfOfUser == null) {
			sb.append("null");
		} else {
			sb.append(this.onBehalfOfUser);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("actor:");
		if (this.actor == null) {
			sb.append("null");
		} else {
			sb.append(this.actor);
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
		sb.append("subStatus:");
		if (this.subStatus == null) {
			sb.append("null");
		} else {
			sb.append(this.subStatus);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("subStatusActionType:");
		if (this.subStatusActionType == null) {
			sb.append("null");
		} else {
			sb.append(this.subStatusActionType);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
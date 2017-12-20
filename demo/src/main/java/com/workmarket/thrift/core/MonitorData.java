package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MonitorData implements Serializable {
	private static final long serialVersionUID = 1L;

	private long timeRunning;
	private List<String> messages;
	private MonitorStatusType status;

	public MonitorData() {
	}

	public MonitorData(long timeRunning, List<String> messages, MonitorStatusType status) {
		this();
		this.timeRunning = timeRunning;
		this.messages = messages;
		this.status = status;
	}

	public long getTimeRunning() {
		return this.timeRunning;
	}

	public MonitorData setTimeRunning(long timeRunning) {
		this.timeRunning = timeRunning;
		return this;
	}

	public boolean isSetTimeRunning() {
		return (timeRunning > 0L);
	}

	public int getMessagesSize() {
		return (this.messages == null) ? 0 : this.messages.size();
	}

	public java.util.Iterator<String> getMessagesIterator() {
		return (this.messages == null) ? null : this.messages.iterator();
	}

	public void addToMessages(String elem) {
		if (this.messages == null) {
			this.messages = new ArrayList<String>();
		}
		this.messages.add(elem);
	}

	public List<String> getMessages() {
		return this.messages;
	}

	public MonitorData setMessages(List<String> messages) {
		this.messages = messages;
		return this;
	}

	public boolean isSetMessages() {
		return this.messages != null;
	}

	public MonitorStatusType getStatus() {
		return this.status;
	}

	public MonitorData setStatus(MonitorStatusType status) {
		this.status = status;
		return this;
	}

	public boolean isSetStatus() {
		return this.status != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof MonitorData)
			return this.equals((MonitorData) that);
		return false;
	}

	private boolean equals(MonitorData that) {
		if (that == null)
			return false;

		boolean this_present_timeRunning = true;
		boolean that_present_timeRunning = true;
		if (this_present_timeRunning || that_present_timeRunning) {
			if (!(this_present_timeRunning && that_present_timeRunning))
				return false;
			if (this.timeRunning != that.timeRunning)
				return false;
		}

		boolean this_present_messages = true && this.isSetMessages();
		boolean that_present_messages = true && that.isSetMessages();
		if (this_present_messages || that_present_messages) {
			if (!(this_present_messages && that_present_messages))
				return false;
			if (!this.messages.equals(that.messages))
				return false;
		}

		boolean this_present_status = true && this.isSetStatus();
		boolean that_present_status = true && that.isSetStatus();
		if (this_present_status || that_present_status) {
			if (!(this_present_status && that_present_status))
				return false;
			if (!this.status.equals(that.status))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_timeRunning = true;
		builder.append(present_timeRunning);
		if (present_timeRunning)
			builder.append(timeRunning);

		boolean present_messages = true && (isSetMessages());
		builder.append(present_messages);
		if (present_messages)
			builder.append(messages);

		boolean present_status = true && (isSetStatus());
		builder.append(present_status);
		if (present_status)
			builder.append(status.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("MonitorData(");
		boolean first = true;

		sb.append("timeRunning:");
		sb.append(this.timeRunning);
		first = false;
		if (!first) sb.append(", ");
		sb.append("messages:");
		if (this.messages == null) {
			sb.append("null");
		} else {
			sb.append(this.messages);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("status:");
		if (this.status == null) {
			sb.append("null");
		} else {
			sb.append(this.status);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}

}
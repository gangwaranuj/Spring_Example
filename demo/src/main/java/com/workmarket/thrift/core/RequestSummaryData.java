package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequestSummaryData implements Serializable {
	private static final long serialVersionUID = 1L;

	private long numberOfRequests;
	private long numberOfSuccessfulRequests;
	private long averageExecutionTime;
	private long numberOfExceptions;
	private List<String> customMessages;

	public RequestSummaryData() {
	}

	public RequestSummaryData(
			long numberOfRequests,
			long numberOfSuccessfulRequests,
			long averageExecutionTime,
			long numberOfExceptions,
			List<String> customMessages) {
		this();
		this.numberOfRequests = numberOfRequests;
		this.numberOfSuccessfulRequests = numberOfSuccessfulRequests;
		this.averageExecutionTime = averageExecutionTime;
		this.numberOfExceptions = numberOfExceptions;
		this.customMessages = customMessages;
	}

	public long getNumberOfRequests() {
		return this.numberOfRequests;
	}

	public RequestSummaryData setNumberOfRequests(long numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
		return this;
	}

	public boolean isSetNumberOfRequests() {
		return (numberOfRequests > 0L);
	}

	public long getNumberOfSuccessfulRequests() {
		return this.numberOfSuccessfulRequests;
	}

	public RequestSummaryData setNumberOfSuccessfulRequests(long numberOfSuccessfulRequests) {
		this.numberOfSuccessfulRequests = numberOfSuccessfulRequests;
		return this;
	}

	public boolean isSetNumberOfSuccessfulRequests() {
		return (numberOfSuccessfulRequests > 0L);
	}

	public long getAverageExecutionTime() {
		return this.averageExecutionTime;
	}

	public RequestSummaryData setAverageExecutionTime(long averageExecutionTime) {
		this.averageExecutionTime = averageExecutionTime;
		return this;
	}

	public boolean isSetAverageExecutionTime() {
		return (averageExecutionTime > 0L);
	}

	public long getNumberOfExceptions() {
		return this.numberOfExceptions;
	}

	public RequestSummaryData setNumberOfExceptions(long numberOfExceptions) {
		this.numberOfExceptions = numberOfExceptions;
		return this;
	}

	public boolean isSetNumberOfExceptions() {
		return (numberOfExceptions > 0L);
	}

	public int getCustomMessagesSize() {
		return (this.customMessages == null) ? 0 : this.customMessages.size();
	}

	public java.util.Iterator<String> getCustomMessagesIterator() {
		return (this.customMessages == null) ? null : this.customMessages.iterator();
	}

	public void addToCustomMessages(String elem) {
		if (this.customMessages == null) {
			this.customMessages = new ArrayList<String>();
		}
		this.customMessages.add(elem);
	}

	public List<String> getCustomMessages() {
		return this.customMessages;
	}

	public RequestSummaryData setCustomMessages(List<String> customMessages) {
		this.customMessages = customMessages;
		return this;
	}

	public boolean isSetCustomMessages() {
		return this.customMessages != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RequestSummaryData)
			return this.equals((RequestSummaryData) that);
		return false;
	}

	private boolean equals(RequestSummaryData that) {
		if (that == null)
			return false;

		boolean this_present_numberOfRequests = true;
		boolean that_present_numberOfRequests = true;
		if (this_present_numberOfRequests || that_present_numberOfRequests) {
			if (!(this_present_numberOfRequests && that_present_numberOfRequests))
				return false;
			if (this.numberOfRequests != that.numberOfRequests)
				return false;
		}

		boolean this_present_numberOfSuccessfulRequests = true;
		boolean that_present_numberOfSuccessfulRequests = true;
		if (this_present_numberOfSuccessfulRequests || that_present_numberOfSuccessfulRequests) {
			if (!(this_present_numberOfSuccessfulRequests && that_present_numberOfSuccessfulRequests))
				return false;
			if (this.numberOfSuccessfulRequests != that.numberOfSuccessfulRequests)
				return false;
		}

		boolean this_present_averageExecutionTime = true;
		boolean that_present_averageExecutionTime = true;
		if (this_present_averageExecutionTime || that_present_averageExecutionTime) {
			if (!(this_present_averageExecutionTime && that_present_averageExecutionTime))
				return false;
			if (this.averageExecutionTime != that.averageExecutionTime)
				return false;
		}

		boolean this_present_numberOfExceptions = true;
		boolean that_present_numberOfExceptions = true;
		if (this_present_numberOfExceptions || that_present_numberOfExceptions) {
			if (!(this_present_numberOfExceptions && that_present_numberOfExceptions))
				return false;
			if (this.numberOfExceptions != that.numberOfExceptions)
				return false;
		}

		boolean this_present_customMessages = true && this.isSetCustomMessages();
		boolean that_present_customMessages = true && that.isSetCustomMessages();
		if (this_present_customMessages || that_present_customMessages) {
			if (!(this_present_customMessages && that_present_customMessages))
				return false;
			if (!this.customMessages.equals(that.customMessages))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_numberOfRequests = true;
		builder.append(present_numberOfRequests);
		if (present_numberOfRequests)
			builder.append(numberOfRequests);

		boolean present_numberOfSuccessfulRequests = true;
		builder.append(present_numberOfSuccessfulRequests);
		if (present_numberOfSuccessfulRequests)
			builder.append(numberOfSuccessfulRequests);

		boolean present_averageExecutionTime = true;
		builder.append(present_averageExecutionTime);
		if (present_averageExecutionTime)
			builder.append(averageExecutionTime);

		boolean present_numberOfExceptions = true;
		builder.append(present_numberOfExceptions);
		if (present_numberOfExceptions)
			builder.append(numberOfExceptions);

		boolean present_customMessages = true && (isSetCustomMessages());
		builder.append(present_customMessages);
		if (present_customMessages)
			builder.append(customMessages);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RequestSummaryData(");
		boolean first = true;

		sb.append("numberOfRequests:");
		sb.append(this.numberOfRequests);
		first = false;
		if (!first) sb.append(", ");
		sb.append("numberOfSuccessfulRequests:");
		sb.append(this.numberOfSuccessfulRequests);
		first = false;
		if (!first) sb.append(", ");
		sb.append("averageExecutionTime:");
		sb.append(this.averageExecutionTime);
		first = false;
		if (!first) sb.append(", ");
		sb.append("numberOfExceptions:");
		sb.append(this.numberOfExceptions);
		first = false;
		if (!first) sb.append(", ");
		sb.append("customMessages:");
		if (this.customMessages == null) {
			sb.append("null");
		} else {
			sb.append(this.customMessages);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

